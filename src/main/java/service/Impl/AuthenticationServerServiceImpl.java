package service.Impl;

import it.unisa.dia.gas.jpbc.Element;
import mapper.UserMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pojo.DO.AuthenticationServer;
import pojo.DO.User;
import pojo.VO.Auditor;
import pojo.VO.Editor;
import pojo.VO.Owner;
import service.AuthenticationServerService;
import util.ArraysUtil;
import util.CryptoUtil;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServerServiceImpl implements AuthenticationServerService {
    @Resource
    private UserMapper userMapper;
    private Logger logger = Logger.getLogger(AuthenticationServerServiceImpl.class);

    /*
    TODO:待优化，考虑工厂方法模式
     */
    @Override
    public User saveUser(int uid, String username, String password, String role) {
        switch (role) {
            case "creator":
                return new Owner(uid, username, password, role);
            case "editor":
                return new Editor(uid, username, password, role);
            case "auditor":
                return new Auditor(uid, username, password, role);
        }
        return null;
    }

    @Override
    public int checkUser(String username, String password, String role) {
        if (userMapper.selByUnamePwdRole(username, password, role) > 0) {
            return userMapper.selByUnamePwdRole(username, password, role);
        } else {
            return -1;
        }
    }

    /**
     * @return C_3, C_4, bP_length, PID_length
     */
    @Override
    public Map<String, Object> receiveC1C2FromUser_sendC3C4ToUser(Map<String, Object> map) {
        //接受C1 C2
        Element C_1 = (Element) map.get("C_1");
        byte[] C_2 = (byte[]) map.get("C_2");
        int ID_length = (int) map.get("ID_length");
        int PWD_length = (int) map.get("PWD_length");
        int aP_length = (int) map.get("aP_length");
        int time_length = (int) map.get("time_length");

        //计算k=s*C_1
        Element k = C_1.duplicate().mulZn(SysParamServiceImpl.s).getImmutable();
        //解密C_2
        byte[] k_hash = CryptoUtil.getHash("SHA-256", k);//将k哈希，使其长度适应aes算法的密钥长度
        byte[] decry_C_2 = CryptoUtil.aes_decrypt(k_hash, C_2);
        //获取信息
        byte[][] splitByte = ArraysUtil.splitByte(decry_C_2, ID_length, PWD_length, aP_length, time_length);
        byte[] ID = splitByte[0];
        byte[] PWD = splitByte[1];
        byte[] aP_byte = splitByte[2];
        byte[] ct = splitByte[3];

        Element aP = SysParamServiceImpl.pairing.getG1().newElementFromBytes(aP_byte);
        //随机数b
        Element b = SysParamServiceImpl.pairing.getZr().newRandomElement().getImmutable();
        //计算会话密钥sk
        Element sk = aP.duplicate().mulZn(b).getImmutable();
        //存储sk
        AuthenticationServer.setSk_as(sk);
        Element bP = SysParamServiceImpl.P.duplicate().mulZn(b).getImmutable();
        //拼接ID、ct、b
        byte[] mergeData1 = ArraysUtil.mergeByte(ID, ct, b.toBytes());
        //虚拟身份PID
        byte[] PID = CryptoUtil.aes_encrypt(k_hash, mergeData1);
        //计算C_3=bP,C_4=E(sk,ID||aP||bP||ct||PID)
        Element C_3 = SysParamServiceImpl.P.duplicate().mulZn(b).getImmutable();
        byte[] mergeData2 = ArraysUtil.mergeByte(ID, aP.toBytes(), bP.toBytes(), ct, PID);
        byte[] sk_hash = CryptoUtil.getHash("SHA-256", sk);
        byte[] C_4 = CryptoUtil.aes_encrypt(sk_hash, mergeData2);

        Map<String, Object> C3C4Map = new HashMap<>();
        C3C4Map.put("C_3", C_3);
        C3C4Map.put("C_4", C_4);
        C3C4Map.put("bP_length", bP.getLengthInBytes());
        C3C4Map.put("PID_length", PID.length);

        try {
            logger.info("第二阶段：AS将C3：" + C_3.toString().substring(0, 15) + " 和C4："
                    + new String(C_4, "ISO8859-1").substring(0, 15) + " 发送给用户");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return C3C4Map;
    }

    @Override
    public byte[] sendSignatureToUser(User user, byte[] encryptedState) {
        byte[] PID = user.getPID().getBytes();
        byte[] state = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedState);
        logger.info("AS解密后的State：" + Arrays.toString(state));
        byte[] state_PID = ArraysUtil.mergeByte(state, PID);
        Element state_PID_hashValue = SysParamServiceImpl.pairing.getG1().newElement().setFromHash(state_PID, 0, state_PID.length);
        Element signature = state_PID_hashValue.duplicate().mulZn(SysParamServiceImpl.s.duplicate()).getImmutable();
        logger.warn("第二阶段：AS给用户发送签名：" + signature.toString());
        return CryptoUtil.aes_encrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), signature.toBytes());
    }

    @Override
    public int createPID(String PID, int uid) {
        return userMapper.updPIDByUid(PID, uid);
    }

    @Override
    public byte[] sendEditSignatureToUser(User user, byte[] encryptedState, byte[] encryptedBlockHash, byte[] encryptedSignature) {
        byte[] PID = user.getPID().getBytes();
        byte[] state = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedState);

        byte[] lastBlockHash = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedBlockHash);

        byte[] signature = CryptoUtil.aes_decrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), encryptedSignature);
        byte[] state_PID_lastBlockHash = ArraysUtil.mergeByte(state, PID, lastBlockHash);
        Element Theta_St = SysParamServiceImpl.pairing.getG1().newElementFromHash(state_PID_lastBlockHash, 0, state_PID_lastBlockHash.length);
        Element thisSignature = Theta_St.duplicate().mulZn(SysParamServiceImpl.s).getImmutable();
        logger.warn("第二阶段：AS将当前签名发送给用户");
        return CryptoUtil.aes_encrypt(CryptoUtil.getHash("SHA-256", AuthenticationServer.getSk_as()), thisSignature.toBytes());
    }
}
