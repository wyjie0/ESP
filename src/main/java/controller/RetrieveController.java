package controller;

import mapper.FileMapper;
import mapper.ProvenanceMapper;
import mapper.RuleMapper;
import mapper.UserMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.DO.File;
import pojo.DO.User;
import pojo.DTO.FileRight;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class RetrieveController {
    @Resource
    private ProvenanceMapper provenanceMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private RuleMapper ruleMapper;
    @Resource
    private UserMapper userMapper;
    private Logger logger = Logger.getLogger(RetrieveController.class);

    @RequestMapping("retrieve")
    @ResponseBody
    public Object retrieve(@RequestParam("operate") String operate, HttpServletRequest req) {
        //省略service
        switch (operate) {
            case "stage":
                //检索生命周期，查询的是所有文件的指定文件周期
                int fid = Integer.parseInt(req.getParameter("fid") == null ? "0" : req.getParameter("fid"));
                if (fid != 0) {
                    List<File> fileList = fileMapper.selFileByFid(fid);
                    if (fileList != null && fileList.size() != 0) {
                        return setTxHash(encodePID_Date(fileList));
                    }
                } else {
                    logger.warn("请求fid为空");
                }
                break;
            case "all":
                //检索所有文件
                List<File> fileList = fileMapper.selAllFileGroupByUid();
                if (fileList != null && fileList.size() != 0) {
                    return setTxHash(encodePID_Date(fileList));
                }
                break;
            case "mine": {
                //检索指定用户的所有文件记录，三种权限均有
                //查询的是所有文件记录（PID会变，根据uid对应权限的fid查询到的是所有文件记录）
                int uid = Integer.parseInt(req.getParameter("uid") == null ? "0" : req.getParameter("uid"));
                if (uid != 0) {
                    return ruleMapper.selAll(uid);
                }
                break;
            }
            case "myFile": {
                //检索指定用户的所有文件记录，三种权限均有
                //查询的是所有文件记录（PID会变，根据uid对应权限的fid查询到的是所有文件记录）
                //分组、排序，同文件只保留最后阶段的记录
                User user = (User) req.getSession().getAttribute("user");
                int uid = user.getUid();
                if (uid != 0) {
                    fileList = fileMapper.selMyFileGroupByUid(uid);
                    List<FileRight> fileRightList = encodePID_Date(fileList);
                    for (FileRight fileRight : fileRightList) {
                        //取出权限
                        List<String> rightList = ruleMapper.selRuleList(uid, fileRight.getFid());
                        for (String s : rightList) {
                            switch (s) {
                                case "own":
                                    fileRight.setOwn(true);
                                    break;
                                case "write":
                                    fileRight.setWrite(true);
                                    break;
                                case "read":
                                    fileRight.setRead(true);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    return setTxHash(fileRightList);
                }
                break;
            }
            case "user":
                //检索所有用户信息
                List<User> users = userMapper.selAll();
                for (User user : users) {
                    //角色转为中文
                    switch (user.getRole()) {
                        case "creator":
                            user.setRole("创建者");
                            break;
                        case "editor":
                            user.setRole("编辑者");
                            break;
                        case "auditor":
                            user.setRole("审计者");
                            break;
                        default:
                            user.setRole("未知身份");
                    }
                    //格式化PID
                    user.setPID(Base64.getEncoder().encodeToString(user.getPID().getBytes()).substring(0, 16));
                }
                return users;
            case "own": {
                //检索指定用户所拥有的文件
                int uid = Integer.parseInt(req.getParameter("uid") == null ? "0" : req.getParameter("uid"));
                if (uid != 0) {
                    //找到fid
                    List<Integer> fidList = ruleMapper.selOwnByUid(uid);
                    fileList = new ArrayList<>();
                    for (Integer _fid : fidList) {
                        //根据fid找到file
                        fileList.addAll(fileMapper.selFileByFid(_fid));
                    }
                    return setTxHash(encodePID_Date(fileList));
                }
                break;
            }
            case "block":
                //检索所有区块数据
                return provenanceMapper.selAllBlock();
        }
        return null;
    }

    /**
     * 为了解决乱码使用Base64编码PID,并取其前16位
     * 对时间戳进行格式化
     * 结果用List<FileRight>包装
     */
    private List<FileRight> encodePID_Date(List<File> fileList) {
        List<FileRight> fileRightList = new ArrayList<>();
        for (File file : fileList) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getOperateDate());
            fileRightList.add(new FileRight(file.getFid(), file.getFileStage(), file.getFileState(), file.getFileName(),
                    file.getFileType(), file.getFileSize(), file.getOperateDate(), file.getOperatePID(),
                    null, null, null, date,
                    Base64.getEncoder().encodeToString(
                            file.getOperatePID().getBytes()).substring(0, 16)));
        }
        return fileRightList;
    }

    /**
     * 向List<FileRight>设置交易哈希值
     */
    private List<FileRight> setTxHash(List<FileRight> fileRightList) {
        List<FileRight> resultList = new ArrayList<>();
        for (FileRight fileRight : fileRightList) {
            String txHash = provenanceMapper.selTxHashByFidFileStage(fileRight.getFid(), fileRight.getFileStage());
            if (txHash != null) {
                fileRight.setTxHash(txHash);
                resultList.add(fileRight);
            }
        }
        return resultList;
    }
}
