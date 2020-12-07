package service.Impl;

import mapper.ProvenanceMapper;
import org.springframework.stereotype.Service;
import pojo.DO.Server;
import service.AuditService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {
    @Resource
    private ProvenanceMapper provenanceMapper;

    @Override
    public List<Server> getAllServer(int fid) {
        return provenanceMapper.selAllByFid(fid);
    }

    @Override
    public int getStageCount(int fid) {
        return provenanceMapper.selCountByFid(fid);
    }
}
