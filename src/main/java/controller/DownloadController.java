package controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.DO.Rule;
import pojo.DO.User;
import service.FileService;
import service.RuleService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class DownloadController {
    @Resource
    private FileService fileServiceImpl;
    @Resource
    private RuleService ruleServiceImpl;

    private Logger logger = Logger.getLogger(DownloadController.class);

    @RequestMapping("download")
    public String download(HttpServletRequest req, HttpServletResponse resp) {
        int fid = Integer.parseInt(req.getParameter("fid"));
        User user = (User) req.getSession().getAttribute("user");
        logger.warn("用户" + user.getUname() + "开始下载文件" + fid);
        Rule rule = new Rule(user.getUid(), fid);
        //拥有、读、写均可下载，但是没有记录的用户无法下载
        if (ruleServiceImpl.selFileRule(rule) != null) {
            logger.warn("用户可以下载文件" + fid);
            if (fileServiceImpl.fileDownload(fid, resp)) {
                logger.error("文件下载成功");
            } else {
                logger.error("文件下载失败");
            }
        }
        logger.warn("用户无法下载文件" + fid);
        return "redirect:/index";
    }
}
