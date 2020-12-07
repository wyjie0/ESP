package controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.DO.File;
import pojo.DO.Rule;
import pojo.DO.User;
import service.FileService;
import service.RuleService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TransferController {
    @Resource
    private FileService fileServiceImpl;
    @Resource
    private RuleService ruleServiceImpl;
    private Logger logger = Logger.getLogger(TransferController.class);

    @RequestMapping("transfer")
    public String transfer(HttpServletRequest req) {
        HttpSession session = req.getSession();
        //清除session
        session.setAttribute(String.valueOf(session.getAttribute("operate")), null);
        User user = (User) session.getAttribute("user");
        int fid = Integer.parseInt(req.getParameter("fid"));
        String uname = req.getParameter("uname");
        String operateRule = req.getParameter("operate");
        logger.warn("用户" + user.getUname() + "转让文件" + fid + "至用户" + uname + "  " + operateRule + "权限");

        Rule rule = new Rule(user.getUid(), fid);
        //查找用户对文件的权限
        List<String> operate = ruleServiceImpl.selFileRule(rule);
        if (operate == null || !operate.contains("own")) {
            logger.error("用户" + user.getUname() + "无权转让文件");
            session.setAttribute("operate", "rejected");
            return "redirect:/index";
        }
        if (operate.contains("own")) {
            logger.error("用户" + user.getUname() + "可以转让文件");
            //PID存的是目标用户的
            //修改：PID存当前操作者
            File file = fileServiceImpl.fileTransfer(fid, uname, user.getPID());
            if (file.getFileStage() > 1) {
                session.setAttribute("file", file);
                logger.error("数据库写文件成功");

                //如果转让的是所有权own，则转让者失去所有权own只有只读read权限
                if (operateRule.equals("own")) {
                    rule.setOperate("read");
                } else {
                    rule.setOperate("own");
                }
                if (ruleServiceImpl.updFileRule(rule) > 0) {
                    //被转让者获得权限
                    if (ruleServiceImpl.insFileRule(uname, fid, operateRule) > 0) {
                        logger.error("数据库写权限成功");
                        session.setAttribute("operate", "transfer");
                        return "forward:/create";
                    }
                }
            } else {
                logger.error("数据库写文件失败");
            }
        }
        return "redirect:/index";
    }
}
