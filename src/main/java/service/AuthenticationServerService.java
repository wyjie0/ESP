package service;

import pojo.DO.User;

import java.util.Map;

public interface AuthenticationServerService {
    /**
     * 保存用户信息
     */
    User saveUser(int uid, String username, String password, String role);

    /**
     * 从数据库中检查是否存在此用户，如果存在返回其id，不存在返回-1
     */
    int checkUser(String username, String password, String role);

    /**
     * AS从用户处收到C1 C2 发送C3 C4给用户
     */
    Map<String, Object> receiveC1C2FromUser_sendC3C4ToUser(Map<String, Object> map);

    /**
     * 响应数据拥有者创建文件
     */
    byte[] sendSignatureToUser(User user, byte[] encryptedState);

    /**
     * 存入（更新）PID
     */
    int createPID(String PID, int uid);

    byte[] sendEditSignatureToUser(User user, byte[] encryptedState, byte[] encryptedBlockHash, byte[] encryptedSignature);
}
