package controller;

import it.unisa.dia.gas.jpbc.Element;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.DO.Audit;
import pojo.DO.Server;
import pojo.DO.User;
import pojo.VO.AuditResult;
import service.AuditService;
import service.Impl.SysParamServiceImpl;
import util.ArraysUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class AuditController {
    @Resource
    private AuditService auditServiceImpl;

    private Logger logger = Logger.getLogger(AuditController.class);

    @RequestMapping("audit")
    @ResponseBody
    List<AuditResult> audit(HttpServletRequest req) {
        //清除session
        User user = (User) req.getSession().getAttribute("user");
        logger.warn("审计者" + user.getUname() + "开始审计文件");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                req.getInputStream(), StandardCharsets.UTF_8))) {
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取到的json字符串
        String acceptJson = sb.toString();
        //将json字符串转为jsonobject对象
        JSONArray json = JSONArray.fromObject(acceptJson);
        //将jsonobject对象转为java对象
        List<Audit> auditList = JSONArray.toList(json, Audit.class);

        //存放审计结果
        List<AuditResult> auditResultList = new ArrayList<>();

        for (Audit audit : auditList) {
            AuditResult result = new AuditResult();
            logger.info("审计文件" + audit.getFid());
            //取出fid
            int fid = audit.getFid();
            List<Server> serverList = auditServiceImpl.getAllServer(fid);
            Server firstServer = serverList.get(0);
            if (firstServer.getFileStage() > 0) {
                logger.info("从服务器中取出第一条溯源记录");
                //取出交易记录
                String[] txData = audit.getTxData().toArray(new String[0]);
                //取出第一条交易记录
                String lastTxData = txData[0];
                logger.info("从区块链中取出第一条交易记录：" + lastTxData);
                //存放第一个审计结果
                if (checkStepFirst(firstServer, lastTxData, true)) {
                    logger.error("文件" + fid + "第一阶段审计成功");
                } else {
                    logger.error("文件" + fid + "第一阶段审计失败");
                }
                result.setCheckPn(checkStepFirst(firstServer, lastTxData, true));
            }
            if (serverList.size() == 1) {
                //文件只有被创建操作，后续的审计默认成功
                result.setCheckRestPn(true);
            }
            for (int i = 1; i < serverList.size(); i++) {
                Server restServer = serverList.get(i);
                if (restServer.getFileStage() > 0) {
                    //取出交易记录
                    String[] txData = audit.getTxData().toArray(new String[0]);
                    //取出最后一条交易记录
                    String lastTxData = txData[i];
                    logger.info("从区块链中取出第" + i + "条交易记录：" + lastTxData);
                    //存放第四个审计结果
                    if (checkStepFirst(restServer, lastTxData, false)) {
                        logger.error("文件" + fid + "第四阶段审计成功");
                    } else {
                        logger.error("文件" + fid + "第四阶段审计失败");
                    }
                    result.setCheckRestPn(checkStepFirst(restServer, lastTxData, false));
                }
            }

            if (fid != 0) {
                for (Server server : serverList) {
                    if (serverList.size() == 1) {
                        result.setCheckSt_n(true);
                        break;
                    }
                    if (server.getFileStage() == 1) {
                        continue;
                    }
                    //存放第二个审计结果
                    if (checkStepSecond(server)) {
                        logger.error("文件" + fid + "第二阶段审计成功");
                    } else {
                        logger.error("文件" + fid + "第二阶段审计失败");
                    }
                    result.setCheckSt_n(checkStepSecond(server));
                }
            }

            //取出阶段数
            int stageCount = audit.getStageCount();
            logger.info("区块链上的文件交易数量：" + stageCount);
            int stageCountFromDB = auditServiceImpl.getStageCount(fid);
            logger.info("云服务器中溯源记录数量：" + stageCountFromDB);
            if (stageCount != stageCountFromDB) {
                logger.error("文件" + fid + "第三阶段审计成功");
                result.setCheckAmount(false);
            } else {
                logger.error("文件" + fid + "第三阶段审计成功");
                result.setCheckAmount(true);
            }
            auditResultList.add(result);
        }
        //resp.getWriter().write(new Gson().toJson(auditResultList));
        return auditResultList;
    }

    /**
     * 第一轮验证
     * 验证最后一条溯源记录
     *
     * @param provenance 需要验证的某条溯源记录
     * @return 验证成功则返回true， 否则返回false
     */
    private boolean checkStepFirst(Server provenance, String lastTxData, boolean isFirst) {
        //分割最后一个溯源信息
        byte[][] splitedBytes;
        byte[] state = null, PID = null, Bl_tail, signature = null, Bl_penult = null;
        try {
            if (isFirst) {
                splitedBytes = ArraysUtil.splitByte(provenance.getProvenance().getBytes("ISO8859-1"),
                        provenance.getStateLength(), provenance.getPIDLength(), provenance.getBlockHashLength(),
                        provenance.getSignatureLength());
                state = splitedBytes[0];
                PID = splitedBytes[1];
                Bl_tail = splitedBytes[2];
                signature = splitedBytes[3];
            } else {
                splitedBytes = ArraysUtil.splitByte(provenance.getProvenance().getBytes("ISO8859-1"),
                        provenance.getStateLength(), provenance.getPIDLength(), provenance.getBlockHashLength(),
                        provenance.getSignatureLength(), provenance.getBlockHashLength());
                state = splitedBytes[0];
                PID = splitedBytes[1];
                Bl_tail = splitedBytes[2];
                signature = splitedBytes[3];
                Bl_penult = splitedBytes[4];
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info("从溯源记录中取出AS签名" + Arrays.toString(signature));

        //将state与PID连接
        byte[] state_PID = ArraysUtil.mergeByte(state, PID);
        //计算state||PID的哈希值
        Element state_PID_hashValue = SysParamServiceImpl.pairing.getZr().newElementFromHash(state_PID, 0, state_PID.length);
        //计算h(state||PID)||sig||BL_penult
        byte[] st_PID_hash_sig_BlPenult;
        if (isFirst) {
            st_PID_hash_sig_BlPenult = ArraysUtil.mergeByte(state_PID_hashValue.toBytes(), signature);
        } else {
            st_PID_hash_sig_BlPenult = ArraysUtil.mergeByte(state_PID_hashValue.toBytes(), signature, Bl_penult);
        }
        //计算交易数据
        Element cal_txData = SysParamServiceImpl.pairing.getZr().newElementFromHash(st_PID_hash_sig_BlPenult, 0, st_PID_hash_sig_BlPenult.length);
        //获取区块链上最后一个区块的最后一个交易数据
        logger.info("根据St，PID，AS签名，上一个区块哈希计算得到的交易记录：" + cal_txData.toString());
        logger.info("从区块链上获取到的交易记录：" + lastTxData);
        return cal_txData.toString().equals(lastTxData);
    }

    /**
     * 第二轮验证
     * 根据最后一个区块的值，计算H(St_n||PID_n||Bl_penult)
     * 验证e(sig, P)与e(H(St_n||PID_n||Bl_penult),P_pub)是否相等
     *
     * @param provenance 最后一条溯源记录
     * @return 如果相等则返回true，否则返回false
     */
    private boolean checkStepSecond(Server provenance) {
        //分割最后一个溯源信息
        byte[][] splitedBytes = new byte[0][];
        try {
            splitedBytes = ArraysUtil.splitByte(provenance.getProvenance().getBytes("ISO8859-1"),
                    provenance.getStateLength(), provenance.getPIDLength(), provenance.getBlockHashLength(),
                    provenance.getSignatureLength(), provenance.getBlockHashLength());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] state = splitedBytes[0];
        byte[] PID = splitedBytes[1];
        byte[] Bl_tail = splitedBytes[2];
        byte[] sig = splitedBytes[3];
        byte[] Bl_penult = splitedBytes[4];

        //将state,PID,Bl_penult连接
        byte[] state_PID_Bl_penult = ArraysUtil.mergeByte(state, PID, Bl_penult);
        //计算H(St_n||PID_n||Bl_penult)
        Element Theta_state = SysParamServiceImpl.pairing.getG1().newElementFromHash(
                state_PID_Bl_penult, 0, state_PID_Bl_penult.length);
        Element signature = SysParamServiceImpl.pairing.getG1().newElementFromBytes(sig).getImmutable();
        logger.info("计算得到的签名：" + signature.toString());

        Element sig_mul_P = SysParamServiceImpl.pairing.pairing(signature, SysParamServiceImpl.P.getImmutable()).getImmutable();

        Element thetaState_mul_P_pub = SysParamServiceImpl.pairing.pairing(Theta_state, SysParamServiceImpl.P_pub).getImmutable();
        logger.info("验证等式左边：" + sig_mul_P.toString());
        logger.info("验证等式右边：" + thetaState_mul_P_pub.toString());
        return sig_mul_P.isEqual(thetaState_mul_P_pub);
    }
}
