package service.Impl;

import it.unisa.dia.gas.jpbc.Element;
import mapper.ProvenanceMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pojo.DO.File;
import pojo.DO.Server;
import pojo.DO.User;
import service.EditorService;
import util.ArraysUtil;
import util.CryptoUtil;
import util.FileHashUtil;

import javax.annotation.Resource;
import java.util.Arrays;

import static service.Impl.SysParamServiceImpl.pairing;

@Service
public class EditServiceImpl implements EditorService {
    @Resource
    private ProvenanceMapper provenanceMapper;
    private Element state;
    private Logger logger = Logger.getLogger(EditServiceImpl.class);

    @Override
    public byte[] sendStateToAS(File file, String ID, User user) {
        String M1;
        if (file.getFileState().equals("destroy")) {
            //删除操作时无文件名，随机生成长度为30的字符串
            M1 = FileHashUtil.stringHashValue(RandomStringUtils.randomAlphabetic(30));
        } else {
            M1 = FileHashUtil.fileHashValue(file.getFilePath());
        }
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
    public byte[] sendLastBlockHash(int fid, User user) {
        Server server = provenanceMapper.selBlockHashPro(fid);
        logger.warn("第一阶段：用户将上一阶段的区块哈希发送给AS");
        return CryptoUtil.aes_encrypt(CryptoUtil.getHash("SHA-256", user.getSk_user()), server.getBlockHash().getBytes());
    }

    @Override
    public byte[] sendLastSignature(int fid, User user) {
        Server server = provenanceMapper.selBlockHashPro(fid);
        String Provenance = server.getProvenance();
        int signatureLength = server.getSignatureLength();
        byte[] lastSignature = ArraysUtil.splitByte(Provenance.getBytes(), Provenance.getBytes().length - signatureLength, signatureLength)[1];
        logger.warn("第一阶段：用户将上一阶段的签名发送给AS");
        return CryptoUtil.aes_encrypt(CryptoUtil.getHash("SHA-256", user.getSk_user()), lastSignature);

    }

    public boolean verifySignature(Element signature, byte[] PID, byte[] lastBlockHash) {
        //计算H(state_PID_BlLast)
        byte[] state_PID_BlLast = ArraysUtil.mergeByte(state.toBytes(), PID, lastBlockHash);
        Element Theta_st = pairing.getG1().newElementFromHash(
                state_PID_BlLast, 0, state_PID_BlLast.length);
        //检验式的左边值
        Element sig_mul_P = pairing.pairing(signature, SysParamServiceImpl.P);
        //检验式的右边值
        Element ThetaSt_mul_P_pub = pairing.pairing(Theta_st, SysParamServiceImpl.P_pub);
        //验证不成功则返回空
        return sig_mul_P.isEqual(ThetaSt_mul_P_pub);
    }
}
