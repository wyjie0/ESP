package service;

import it.unisa.dia.gas.jpbc.Element;
import pojo.DO.File;
import pojo.DO.User;

import java.util.Map;

public interface OwnerService {

    /**
     * 用户将C1 C2发给AS
     */
    Map<String, Object> sendC1C2ToAS(User user);

    /**
     * 用户将文件状态发给AS
     */
    byte[] sendStateToAS(File file, String ID, User user);

    /**
     * 计算会话密钥，返回byte[] PID
     */
    User getSessionKey(Map<String, Object> map, User user);

    /**
     * 用户验证AS的签名
     */
    boolean verifySignature(Element signature, byte[] PID);

    /**
     * 用户计算交易数据
     */
    Element sendTxData(byte[] signature, User user);
}
