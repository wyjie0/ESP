package service.Impl;

import mapper.ProvenanceMapper;
import org.springframework.stereotype.Service;
import pojo.DO.Server;
import service.ServerService;

import javax.annotation.Resource;

@Service
public class ServerServiceImpl implements ServerService {
    @Resource
    private ProvenanceMapper provenanceMapper;

    @Override
    public int createProvenance(Server server) {
        return provenanceMapper.createPro(server.getFid(), server.getFileStage(), server.getBlockHash(),
                server.getTxHash(), server.getProvenance(), server.getStateLength(), server.getPIDLength(),
                server.getBlockHashLength(), server.getSignatureLength());
    }
}
