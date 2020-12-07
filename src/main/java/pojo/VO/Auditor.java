package pojo.VO;

import pojo.DO.User;

public class Auditor extends User {
    public Auditor(int uid, String uname, String password, String role) {
        super(uid, uname, password, role);
    }
}
