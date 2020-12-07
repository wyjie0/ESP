package service.Impl;

import mapper.FileMapper;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Service;
import pojo.DO.File;
import pojo.DO.User;
import service.FileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Resource
    private FileMapper fileMapper;
    private String saveFilePath;//存放文件的全路径，包含文件名
    private Logger logger = Logger.getLogger(FileServiceImpl.class);

    @Override
    public File fileUpload(HttpServletRequest req, String fileState) {
        File returnFile = new File();
        returnFile.setFileState(fileState);
        //创建/编辑
        User user = (User) req.getSession().getAttribute("user");
        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        //存放文件的路径
        String savePath = req.getServletContext().getRealPath("/WEB-INF/upload");
        java.io.File file = new java.io.File(savePath);
        //判断上传文件的保存目录是否存在
        if (!file.exists() && !file.isDirectory()) {
            logger.error(savePath + "目录不存在，需要创建");
            //创建目录
            file.mkdir();
        }

        //还可以使用SpringMVC的MultipartFile进行文件上传
        //使用Apache文件上传组件处理文件上传步骤：
        //1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置工厂的缓冲区的大小为100KB
        factory.setSizeThreshold(1024 * 100);
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        //监听文件上传精度
        upload.setProgressListener((l, l1, i) -> logger.info("文件大小为：" + l1 + "，当前已处理：" + l)
        );
        //解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");
        //3、判断提交上来的数据是否是上传表单的数据
        if (!ServletFileUpload.isMultipartContent(req)) {
            //按照传统方式获取数据
            return returnFile;
        }
        //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
        List<FileItem> list = null;
        try {
            list = upload.parseRequest(new ServletRequestContext(req));
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        //表单其他字段
        String name;
        String value = "";
        for (FileItem item : list) {
            if (item.isFormField()) {
                //如果fileitem中封装的是普通输入项的数据
                name = item.getFieldName();
                try {
                    value = item.getString("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                returnFile.setFid(Integer.parseInt(value));
            } else {
                //如果fileitem中封装的是上传文件
                //得到上传的文件名称
                String fileName = item.getName();
                if (fileName == null || fileName.trim().equals("")) {
                    continue;
                }
                //记录文件信息
                returnFile.setOperateDate(System.currentTimeMillis());
                returnFile.setFileSize(item.getSize());
                returnFile.setFileType(item.getContentType());
                //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                logger.info("上传的文件名：" + fileName);
                returnFile.setFileName(fileName);
                if (fileState.equals("create")) {
                    //创建操作
                    int newFid = selectMaxFid() + 1;
                    saveFilePath = savePath + "\\" + newFid + "_" + "1_" + fileName;
                } else if (fileState.equals("edit")) {
                    //编辑操作
                    //与数据库代码耦合
                    int lastStage = fileMapper.selMaxStage(Integer.parseInt(value)) + 1;
                    saveFilePath = savePath + "\\" + value + "_" + lastStage + "_" + fileName;
                }
                //上传后的文件名为 根路径\\文件id_阶段号_文件名
                logger.info("文件保存路径:" + saveFilePath);
                returnFile.setFilePath(saveFilePath);
                returnFile.setOperatePID(user.getPID());
                //创建一个缓冲区
                byte[] buffer = new byte[1024];
                //判断输入流中的数据是否已经读完的标识
                int len;
                java.io.File tempFile = new java.io.File(saveFilePath);
                byte[] bytes = new byte[(int) tempFile.length()];
                try (//获取item中的上传文件的输入流
                     InputStream in = item.getInputStream();
                     //创建一个文件输出流
                     FileOutputStream out = new FileOutputStream(saveFilePath)) {
                    //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                    while ((len = in.read(buffer)) > 0) {
                        //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + fileName)当中
                        out.write(buffer, 0, len);
                    }
                    in.read(bytes);
                    returnFile.setFile(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //删除处理文件上传时生成的临时文件
                item.delete();
            }
        }
        return returnFile;
    }

    public File fileCreate(File file) {
        int lastFid = selectMaxFid();
        if (fileMapper.createFile(lastFid + 1, file.getFileName(), file.getFileType(), file.getFileSize(),
                file.getOperateDate(), file.getOperatePID(), file.getFile(), file.getFilePath()) > 0) {
            file.setFid(lastFid + 1);
            file.setFileStage(1);
            return file;
        }
        return null;
    }

    public File fileEdit(File file) {
        int lastStage = fileMapper.selMaxStage(file.getFid());
        String fileName = file.getFileName() == null || file.getFileName().equals("") ? "" : file.getFileName();
        if (fileMapper.editFile(file.getFid(), lastStage + 1, file.getFileState(),
                fileName, file.getFileType(), file.getFileSize(),
                file.getOperateDate(), file.getOperatePID(), file.getFile(), file.getFilePath()) > 0) {
            file.setFileStage(lastStage + 1);
            return file;
        }
        return null;
    }

    @Override
    public File fileDelete(File file) {
        int lastStage = fileMapper.selMaxStage(file.getFid());
        if (fileMapper.deleteFile(file.getFid(), lastStage + 1, file.getOperateDate(), file.getOperatePID()) > 0) {
            file.setFileStage(lastStage + 1);
            return file;
        }
        return null;
    }

    @Override
    public File fileTransfer(int fid, String uname, String PID) {
        int lastStage = fileMapper.selMaxStage(fid);
        File file = fileMapper.selFile(fid, lastStage);
        if (fileMapper.editFile(fid, lastStage + 1, "transfer",
                file.getFileName(), file.getFileType(), file.getFileSize(),
                System.currentTimeMillis(), PID, file.getFile(), file.getFilePath()) > 0) {
            file.setFileStage(lastStage + 1);
            file.setFileState("transfer");
            file.setOperateDate(System.currentTimeMillis());
            file.setOperatePID(PID);
            return file;
        }
        return null;
    }


    @Override
    public boolean fileDownload(int fid, HttpServletResponse resp) {
        //下载的是最后一个阶段的文件
        int lastStage = fileMapper.selMaxStage(fid);
        String filePath = fileMapper.selFilePathByFidAndFileStage(fid, lastStage);
        //找到第二个"_"出现的位置 避免146_1_dualshock_4_hungarian.txt类型的报错
        String tempFileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
        String filestage_FileName = tempFileName.substring(tempFileName.indexOf("_") + 1);
        String realName = filestage_FileName.substring(filestage_FileName.indexOf("_") + 1);
        //设置响应头，控制浏览器下载该文件
        try {
            resp.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realName, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try (//读取要下载的文件，保存到文件输入流
             FileInputStream in = new FileInputStream(filePath);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                //输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int selectMaxFid() {
        return fileMapper.selMaxFid();
    }
}
