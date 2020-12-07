package controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.DO.File;
import pojo.DO.Server;
import service.ServerService;
import util.ArraysUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Controller
public class ServerController {
    @Resource
    private ServerService serverServiceImpl;
    private Logger logger = Logger.getLogger(ServerController.class);

    @ResponseBody
    @RequestMapping("server")
    public int server(HttpServletRequest req) throws UnsupportedEncodingException {
        HttpSession session = req.getSession();
        logger.warn("第三阶段：用户写溯源记录");
        String txHash = req.getParameter("txHash");
        logger.info("交易哈希地址为：" + txHash);
        String blockHash = req.getParameter("blockHash");
        logger.info("交易区块哈希地址为：" + blockHash);

        byte[] state = (byte[]) session.getAttribute("state");
        String sPID = (String) session.getAttribute("PID");
        byte[] PID = sPID.getBytes();
        byte[] signature = (byte[]) session.getAttribute("signature");
        File file = (File) session.getAttribute("file");
        String operate = (String) session.getAttribute("operate");
        if (file != null) {
            if (blockHash != null && txHash != null) {
                Server server = new Server();
                server.setFid(file.getFid());
                server.setFileStage(file.getFileStage());
                server.setBlockHash(blockHash);
                server.setTxHash(txHash);
                server.setStateLength(state.length);
                server.setPIDLength(PID.length);
                server.setBlockHashLength(blockHash.getBytes().length);
                if (signature != null) {
                    server.setSignatureLength(signature.length);
                }
                byte[] provenance;
                if (operate.equals("upload")) {
                    provenance = ArraysUtil.mergeByte(state, PID, blockHash.getBytes(), signature);
                    server.setProvenance(new String(provenance, "ISO8859-1"));
                } else if (operate.equals("edit") || operate.equals("delete") || operate.equals("transfer")) {
                    byte[] lastBlockHash = (byte[]) session.getAttribute("lastBlockHash");
                    provenance = ArraysUtil.mergeByte(state, PID, blockHash.getBytes(), signature, lastBlockHash);
                    server.setProvenance(new String(provenance, "ISO8859-1"));
                    logger.info("溯源记录为：" + Arrays.toString(server.getProvenance().getBytes("ISO8859-1")));
                }

                if (serverServiceImpl.createProvenance(server) > 0) {
                    logger.error("数据库写入溯源记录成功");
                    if (operate.equals("upload")) {
                        return 0;
                    } else if (operate.equals("edit") || operate.equals("delete") || operate.equals("transfer")) {
                        return 0;
                    }

                    //不要清除用户登录的session
                } else {
                    logger.error("数据库写入溯源记录失败");
                    return 1;
                }
            }
        }
        logger.error("上传文件为空");
        return 2;
    }
}
