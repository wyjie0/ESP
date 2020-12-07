package service;

import it.unisa.dia.gas.jpbc.Element;
import pojo.DO.File;
import pojo.DO.User;

public interface EditorService {
    /**
     * 用户将文件状态发给AS
     */
    byte[] sendStateToAS(File file, String ID, User user);

    /**
     * 用户验证AS的签名
     */
    boolean verifySignature(Element signature, byte[] PID, byte[] lastBlockHash);

    /**
     * 用户将上一个区块hash发给AS
     */
    byte[] sendLastBlockHash(int fid, User user);

    /**
     * 用户将上一个签名发给AS
     */
    byte[] sendLastSignature(int fid, User user);
}
