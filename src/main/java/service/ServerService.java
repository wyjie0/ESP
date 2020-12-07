package service;

import pojo.DO.Server;

public interface ServerService {
    /**
     * 创建溯源记录
     */
    int createProvenance(Server server);
}
