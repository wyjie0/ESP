package service;

import pojo.DO.Server;

import java.util.List;

public interface AuditService {
    /**
     * 获取fid文件的所有溯源记录
     */
    List<Server> getAllServer(int fid);

    /**
     * 获取fid文件的所有溯源记录的个数
     */
    int getStageCount(int fid);
}
