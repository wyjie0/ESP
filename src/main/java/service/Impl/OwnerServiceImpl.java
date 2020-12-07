package service.Impl;

import it.unisa.dia.gas.jpbc.Element;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pojo.DO.File;
import pojo.DO.User;
import service.OwnerService;
import util.ArraysUtil;
import util.CryptoUtil;
import util.FileHashUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static service.Impl.SysParamServiceImpl.*;

@Service
public class OwnerServiceImpl implements OwnerService {
    private Element aP;
    private Integer uid;
    private Element state;
    private Logger logger = Logger.getLogger(OwnerServiceImpl.class);

    /**
     * 返回C_1, C_2, ID_length, PWD_length, aP_length, time_length
     */
    @Override
    public Map<String, Object> sendC1C2ToAS(User user) {
        uid = user.getUid();
        String password = user.getPassword();
        //用户选择的随机数r_1,a和当前时间
        Element r_1 = pairing.getZr().newRandomElement().getImmutable();
        Element a = pairing.getZr().newRandomElement().getImmutable();
        long currentTime = System.currentTimeMillis();

        Element C_1 = P.duplicate().mulZn(r_1).getImmutable();
        Element k = P_pub.duplicate().mulZn(r_1).getImmutable();
        aP = P.duplicate().mulZn(a).getImmutable();

        byte[] k_hash = CryptoUtil.getHash("SHA-256", k);//将k哈希，使其长度适应aes算法的密钥长度

        //拼接uid、PWD、aP、ct
        byte[] data = ArraysUtil.mergeByte(uid.toString().getBytes(),
                password.getBytes(),
                aP.toBytes(),
                Long.toString(currentTime).getBytes());

        //计算C_2 = E(k,uid||PWD||aP||ct)
        byte[] C_2 = CryptoUtil.aes_encrypt(k_hash, data);

        //将C_1,C_2以及各个部分的长度传给服务器
        Map<String, Object> C1C2Map = new HashMap<>();
        C1C2Map.put("C_1", C_1);
        C1C2Map.put("C_2", C_2);
        C1C2Map.put("ID_length", uid.toString().getBytes().length);
        C1C2Map.put("PWD_length", password.getBytes().length);
        C1C2Map.put("aP_length", aP.getLengthInBytes());
        C1C2Map.put("time_length", Long.toString(currentTime).getBytes().length);
        C1C2Map.put("a", a);

        try {
            logger.info("第一阶段：用户将C1：" + C_1.toString().substring(0, 15) + " 和C2：" +
                    new String(C_2, "ISO8859-1").substring(0, 15) + " 发送给AS");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return C1C2Map;
    }

    @Override
    public byte[] sendStateToAS(File file, String ID, User user) {
        String M1 = FileHashUtil.fileHashValue(file.getFilePath());
        logger.info("数据文件在其生命周期第一阶段的内容：" + M1);
        byte[] M1_ID = ArraysUtil.mergeByte(M1.getBytes(), ID.getBytes());
        state = pairing.getZr().newElement().setFromHash(M1_ID, 0, M1_ID.length);
        logger.info("加密前的状态信息State：" + Arrays.toString(state.toBytes()));
        byte[] encryptedState = CryptoUtil.aes_encrypt(CryptoUtil.getHash("SHA-256", user.getSk_user()), state.toBytes());
        logger.info("加密后的状态信息State：" + Arrays.toString(encryptedState));
        logger.warn("第一阶段：用户将加密后的状态信息发送给AS");
        return encryptedState;
    }

    @Override
    public User getSessionKey(Map<String, Object> map, User user) {
        //获取C_3,C_4,以及各部分消息长度
        Element C_3 = (Element) map.get("C_3");
        byte[] C_4 = (byte[]) map.get("C_4");
        int ID_length = (int) map.get("ID_length");
        int aP_length = (int) map.get("aP_length");
        int bP_length = (int) map.get("bP_length");
        int time_length = (int) map.get("time_length");
        int PID_length = (int) map.get("PID_length");
        Element a = (Element) map.get("a");

        //计算会话密钥
        Element sk = C_3.duplicate().mulZn(a).getImmutable();
        user.setSk_user(sk);
        byte[] sk_hash = CryptoUtil.getHash("SHA-256", sk);
        //利用会话密钥解密C_4,分割明文
        byte[] decry_C_4 = CryptoUtil.aes_decrypt(sk_hash, C_4);
        byte[][] splitByte = ArraysUtil.splitByte(decry_C_4, ID_length, aP_length, bP_length, time_length, PID_length);
        //确认ID||aP||bP||ct的合法性
        byte[] bP = splitByte[2];
        byte[] ct = splitByte[3];
        if (!uid.toString().equals(new String(splitByte[0]))) {
            logger.warn("用户验证ID无法通过");
            return null;
        } else {
            logger.warn("用户验证ID通过");
        }

        Element bPElement = pairing.getG1().newElementFromBytes(bP);
        if (!C_3.isEqual(bPElement)) {
            logger.warn("用户验证bP无法通过");
            return null;
        } else {
            logger.warn("用户验证bP通过");
        }

        Element aPElement = pairing.getG1().newElementFromBytes(splitByte[1]);
        if (!aP.isEqual(aPElement)) {
            logger.warn("用户验证aP无法通过");
            return null;
        } else {
            logger.warn("用户验证aP通过");
        }

        byte[] PID = splitByte[4];
        user.setPID(new String(PID));
        return user;
    }

    @Override
    public boolean verifySignature(Element signature, byte[] PID) {
        Element signature_mul_P = pairing.pairing(signature, P);
        logger.info("用户计算验证等式的左边：" + signature_mul_P.toString());
        byte[] state_PID = ArraysUtil.mergeByte(state.toBytes(), PID);
        Element state_PID_hashValue = pairing.getG1().newElement().setFromHash(state_PID, 0, state_PID.length);
        Element state_PID_hashValue_mul_P_pub = pairing.pairing(state_PID_hashValue, P_pub);
        logger.info("用户计算验证等式的右边：" + state_PID_hashValue_mul_P_pub);
        return signature_mul_P.equals(state_PID_hashValue_mul_P_pub);
    }

    @Override
    public Element sendTxData(byte[] signature, User user) {
        byte[] st1_PID1 = ArraysUtil.mergeByte(state.toBytes(), user.getPID().getBytes());
        Element st1_PID1_Hash = pairing.getZr().newElement().setFromHash(st1_PID1, 0, st1_PID1.length);
        byte[] st1_PID1_Hash_signature = ArraysUtil.mergeByte(st1_PID1_Hash.toBytes(), signature);
        return pairing.getZr().newElement().setFromHash(st1_PID1_Hash_signature, 0, st1_PID1_Hash_signature.length);
    }
}
