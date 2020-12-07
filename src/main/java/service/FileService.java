package service;

import pojo.DO.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FileService {
    /**
     * 上传文件
     */
    File fileUpload(HttpServletRequest req, String fileState);

    /**
     * 找到数据库中最大的fid，fid自增后插入新的记录，返回的是赋主键值后的file
     */
    File fileCreate(File file);

    /**
     * 根据fid找到最大的stage，stage自增后插入数据库
     */
    File fileEdit(File file);

    /**
     * 根据fid找到最大的stage，stage自增后插入数据库
     */
    File fileDelete(File file);

    /**
     * 转让，根据fid找到最大的stage，stage赋值为transfer插入数据库
     */
    File fileTransfer(int fid, String uname, String PID);

    /**
     * 文件下载
     */
    boolean fileDownload(int fid, HttpServletResponse resp);

    /**
     * 获取文件最大Fid
     */
    int selectMaxFid();
}
