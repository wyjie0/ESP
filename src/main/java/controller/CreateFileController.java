package controller;

import it.unisa.dia.gas.jpbc.Element;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.DO.AuthenticationServer;
import pojo.DO.File;
import pojo.DO.User;
import service.AuthenticationServerService;
import service.EditorService;
import service.Impl.SysParamServiceImpl;
import service.OwnerService;
import util.ArraysUtil;
import util.CryptoUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class CreateFileController {
    @Resource
    private OwnerService ownerServiceImpl;
    @Resource
    private EditorService editorServiceImpl;
    @Resource
    private AuthenticationServerService authenticationServerServiceImpl;

    private Logger logger = Logger.getLogger(CreateFileController.class);
    private File file = null;
    private User user = null;

    @RequestMapping("create")
    public String createFile(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String operate = (String) session.getAttribute("operate");
        user = (User) session.getAttribute("user");
        file = (File) session.getAttribute("file");
        if (user.getRole().equals("creator")) {
            switch (operate) {
                case "upload":
                    logger.warn("用户开始创建文件溯源记录：上传操作");
                    //Owner owner = (Owner) user;
                    //创建文件
                    byte[] encryptedState = ownerServiceImpl.sendStateToAS(file, Integer.valueOf(user.getUid()).toString(), user);
                    byte[] encryptedSignature = authenticationServerServiceImpl.sendSignatureToUser(user, encryptedState);
                    byte[] signature = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedSignature);
                    byte[] state = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedState);

                    //存入session
                    session.setAttribute("state", state);
                    session.setAttribute("PID", user.getPID());
                    session.setAttribute("signature", signature);
                    if (ownerServiceImpl.verifySignature(SysParamServiceImpl.pairing.getG1().newElementFromBytes(signature),
                            user.getPID().getBytes())) {
                        logger.error("用户接受AS的签名");
                        //生成区块链交易数据
                        Element st1_PID1_Hash = ownerServiceImpl.sendTxData(signature, user);
                        logger.info("用户生成交易数据：" + st1_PID1_Hash.toString());
                        file.setTxData(st1_PID1_Hash.toString());
                        file.setFile(null);
                        session.setAttribute("uploadCode", "1");
                        //使用请求转发会多次提交
                        return "redirect:/index";
                    } else {
                        logger.error("用户拒绝AS的签名");
                        session.setAttribute("uploadCode", "0");
                    }
                    break;
                case "edit":
                    //Editor editor = (Editor) user;
                    logger.warn("用户开始创建文件溯源记录：编辑操作");
                    return "forward:/editFile?operateCode=editCode";
                case "delete":
                    logger.warn("用户开始创建文件溯源记录：删除操作");
                    return "forward:/editFile?operateCode=deleteCode";
                case "transfer":
                    logger.warn("用户开始创建文件溯源记录：转让操作");
                    return "forward:/editFile?operateCode=transferCode";
            }
        }
        session.setAttribute("uploadCode", "0");
        return "redirect:/index";
    }

    @RequestMapping("editFile")
    private String editFile(HttpSession session, @RequestParam("operateCode") String operateCode) {
        byte[] encryptedState = editorServiceImpl.sendStateToAS(file, Integer.valueOf(user.getUid()).toString(), user);
        byte[] encryptedlastBlockHash = editorServiceImpl.sendLastBlockHash(file.getFid(), user);
        byte[] encryptedlastSignature = editorServiceImpl.sendLastSignature(file.getFid(), user);
        byte[] encryptedSignature = authenticationServerServiceImpl.sendEditSignatureToUser(user, encryptedState, encryptedlastBlockHash, encryptedlastSignature);
        byte[] signature = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedSignature);
        Element newSignature = SysParamServiceImpl.pairing.getG1().newElementFromBytes(signature);
        byte[] state = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", user.getSk_user()), encryptedState);
        byte[] lastBlockHash = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", user.getSk_user()), encryptedlastBlockHash);
        //存入session
        session.setAttribute("state", state);
        session.setAttribute("PID", user.getPID());
        session.setAttribute("signature", signature);
        session.setAttribute("lastBlockHash", lastBlockHash);
        if (editorServiceImpl.verifySignature(newSignature, user.getPID().getBytes(), lastBlockHash)) {
            logger.error("用户接受AS的签名");
            //生成区块链交易数据
            byte[] state_PID = ArraysUtil.mergeByte(state, user.getPID().getBytes());
            Element state_PID_hashValue = SysParamServiceImpl.pairing.getZr().newElementFromHash(state_PID, 0, state_PID.length);
            byte[] state_PID_hashValue_sig_lastBl = ArraysUtil.mergeByte(state_PID_hashValue.toBytes(), newSignature.toBytes(), lastBlockHash);
            Element txData = SysParamServiceImpl.pairing.getZr().newElementFromHash(state_PID_hashValue_sig_lastBl, 0, state_PID_hashValue_sig_lastBl.length);
            file.setTxData(txData.toString());
            logger.info("用户产生交易数据：" + txData.toString());
            file.setFile(null);
            session.setAttribute(operateCode, "1");
            return "redirect:/index";
        }
        logger.error("用户拒绝AS的签名");
        session.setAttribute(operateCode, "0");
        return "redirect:/index";
    }
}
