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
public class EditFileController {
    @Resource
    private FileService fileServiceImpl;
    @Resource
    private RuleService ruleServiceImpl;

    private Logger logger = Logger.getLogger(EditFileController.class);

    @RequestMapping("edit")
    public String editFile(HttpServletRequest req) {
        HttpSession session = req.getSession();
        //清除session
        session.setAttribute(String.valueOf(session.getAttribute("operate")), null);
        User user = (User) session.getAttribute("user");
        logger.warn("用户" + user.getUname() + "开始编辑文件");

        //同样的上传文件
        File file = fileServiceImpl.fileUpload(req, "edit");

        //用户操作权限判断
        Rule rule = new Rule(user.getUid(), file.getFid());
        List<String> operate = ruleServiceImpl.selFileRule(rule);
        //短路判断，防止operate为空指针
        if (operate == null || operate.contains("read")) {
            logger.error("用户无权编辑文件");
            session.setAttribute("operate", "rejected");
            return "redirect:/index";
        }
        if (operate.contains("own") || operate.contains("write")) {
            logger.error("用户可以编辑文件");
            if (file.getFid() > 0) {
                logger.error("编辑文件成功");
                //填充fileStage字段
                file = fileServiceImpl.fileEdit(file);
            } else {
                logger.error("编辑文件失败");
                return "redirect:/index";
            }
        }


        if (file.getFileStage() > 1) {
            session.setAttribute("file", file);
            logger.error("数据库写文件成功");
            session.setAttribute("operate", "edit");
            return "forward:/create";
        } else {
            logger.error("数据库写文件失败");
            return "redirect:/index";
        }
    }
}
