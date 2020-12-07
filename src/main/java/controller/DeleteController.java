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
public class DeleteController {
    @Resource
    private FileService fileServiceImpl;
    @Resource
    private RuleService ruleServiceImpl;

    private Logger logger = Logger.getLogger(DeleteController.class);

    @RequestMapping("delete")
    public String delete(HttpServletRequest req) {
        int fid = Integer.parseInt(req.getParameter("fid"));

        HttpSession session = req.getSession();
        //清除session
        session.setAttribute(String.valueOf(session.getAttribute("operate")), null);
        User user = (User) session.getAttribute("user");
        logger.warn("用户" + user.getUname() + "开始删除文件" + fid);
        if (user.getRole().equals("creator")) {
            //判断用户操作权限
            Rule rule = new Rule(user.getUid(), fid);
            List<String> operate = ruleServiceImpl.selFileRule(rule);
            //只读read无法删除
            if (operate == null || !operate.contains("own")) {
                logger.warn("用户无权删除文件");
                session.setAttribute("operate", "rejected");
                return "redirect:/index";
            }
            //读写write可以删除（写入空文件就相当于删除）
            //拥有own可以删除
            if (operate.contains("own") || operate.contains("write")) {
                File file = new File();
                file.setFid(fid);
                file.setOperatePID(user.getPID());
                file.setOperateDate(System.currentTimeMillis());
                file.setFileState("destroy");

                //填充fileStage
                file = fileServiceImpl.fileDelete(file);

                if (file.getFileStage() > 1) {
                    session.setAttribute("file", file);
                    logger.error("数据库写文件成功");
                    session.setAttribute("operate", "delete");
                    //写入溯源记录和区块之前删除所有关联此文件的权限
                    if (ruleServiceImpl.delByFid(fid) > 0) {
                        logger.warn("已删除关联此文件的用户权限");
                    }
                    return "forward:/create";
                } else {
                    logger.error("数据库写文件失败");
                    return "redirect:/index";
                }
            }
        }
        logger.error("非创建者无法删除文件");
        return "redirect:/index";
    }
}
