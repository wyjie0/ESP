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

@Controller
public class UploadFileController {
    @Resource
    private FileService fileServiceImpl;
    @Resource
    private RuleService ruleServiceImpl;
    private Logger logger = Logger.getLogger(UploadFileController.class);

    @RequestMapping("upload")
    public String uploadFile(HttpServletRequest req) {
        HttpSession session = req.getSession();
        //清除session
        session.setAttribute(String.valueOf(session.getAttribute("operate")), null);
        User user = (User) session.getAttribute("user");
        logger.warn("用户" + user.getUname() + "开始上传文件");
        //先上传文件，数据库的事务打开时间和文件上传服务器没有交集，避免多用户上传时数据库长期得不到释放的可能性降低了宕机的可能性，提高了系统性能
        //一般上传文件是将文件上传至服务器，数据库只保留服务器中的文件路径。本项目将文件及文件路径都存入数据库，当文件过大时有可能产生内存溢出
        File file = fileServiceImpl.fileUpload(req, "create");
        if (file != null) {
            logger.error("上传文件成功");
            //填充fid fileStage字段
            file = fileServiceImpl.fileCreate(file);
        } else {
            logger.error("上传文件失败");
            return "redirect:/index";
        }

        if (file.getFid() > 0) {
            logger.error("数据库写文件成功");
            session.setAttribute("file", file);
            Rule rule = new Rule(user.getUid(), file.getFid(), "own");
            if (ruleServiceImpl.insFileRule(rule) > 0) {
                logger.error("数据库写文件权限成功");
                session.setAttribute("operate", "upload");
                return "forward:/create";
            } else {
                logger.error("数据库写文件权限失败");
            }
        } else {
            logger.error("数据库写文件失败");
        }
        //req.getSession().invalidate();
        return "redirect:/index";
    }
}
