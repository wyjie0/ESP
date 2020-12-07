package controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.DO.User;
import pojo.VO.Auditor;
import pojo.VO.Editor;
import pojo.VO.Owner;
import service.AuthenticationServerService;
import service.OwnerService;
import service.SysParamService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthenticationServerController {
    @Resource
    private AuthenticationServerService authenticationServerServiceImpl;

    @Resource
    private SysParamService sysParamServiceImpl;

    @Resource
    private OwnerService ownerServiceImpl;

    /**
     * 登录验证
     */
    @RequestMapping("as")
    public String authenticationServer(HttpServletRequest req) {
        Logger logger = Logger.getLogger(AuthenticationServerController.class);
        //获取表单数据
        String uname = req.getParameter("username");
        String password = req.getParameter("password");
        String role = req.getParameter("userIdentity");
        int uid = authenticationServerServiceImpl.checkUser(uname, password, role);

        //根据不同用户角色返回不同对象
        User user = authenticationServerServiceImpl.saveUser(uid, uname, password, role);
        logger.warn("用户" + user.getUname() + "开始登录");
        //用户登录
        if (uid > 0) {
            logger.warn("AS验证用户ID：" + uid + "和pwd：" + password + "通过");
            logger.error("用户" + user.getUname() + "登录成功");
            //初始化参数
            sysParamServiceImpl.init();
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            //密钥协商
            logger.warn("AS与用户开始协商会话密钥");
            Map<String, Object> C1C2Map = ownerServiceImpl.sendC1C2ToAS(user);
            Map<String, Object> C3C4Map = authenticationServerServiceImpl.receiveC1C2FromUser_sendC3C4ToUser(C1C2Map);
            Map<String, Object> map = new HashMap<>();
            map.putAll(C1C2Map);
            map.putAll(C3C4Map);
            user = ownerServiceImpl.getSessionKey(map, user);
            //数据库存入PID
            if (authenticationServerServiceImpl.createPID(user.getPID(), user.getUid()) > 0) {
                logger.error("数据库存入PID成功");
            } else {
                logger.error("数据库存入PID失败");
            }
            //TODO:add editor
            if (user instanceof Owner || user instanceof Editor) {
                session.setAttribute("role", "owner");
                return "forward:/code?code=1";
            } else if (user instanceof Auditor) {
                session.setAttribute("role", "auditor");
                return "forward:/code?code=2";
            }
        } else if (uid == -1) {
            logger.error("用户" + user.getUname() + "登录失败");
            return "redirect:/login";
        }
        return null;
    }

    /**
     * 状态码返回给前台：owner=1 auditor=2
     */
    @RequestMapping("code")
    @ResponseBody
    public int returnRuleCode(@RequestParam("code") int code) {
        return code;
    }
}
