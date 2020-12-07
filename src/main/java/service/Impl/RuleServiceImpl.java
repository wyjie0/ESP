package service.Impl;

import mapper.RuleMapper;
import mapper.UserMapper;
import org.springframework.stereotype.Service;
import pojo.DO.Rule;
import service.RuleService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RuleServiceImpl implements RuleService {
    @Resource
    private RuleMapper ruleMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public int updFileRule(Rule rule) {
        return ruleMapper.updRule(rule.getUid(), rule.getFid(), rule.getOperate());
    }

    @Override
    public int insFileRule(Rule rule) {
        return ruleMapper.insRule(rule.getUid(), rule.getFid(), rule.getOperate());
    }

    @Override
    public List<String> selFileRule(Rule rule) {
        return ruleMapper.selRule(rule.getUid(), rule.getFid());
    }

    @Override
    public int insFileRule(String uname, int fid, String operate) {
        int uid = userMapper.selUidByUname(uname);
        return ruleMapper.insRule(uid, fid, operate);
    }

    @Override
    public int delByFid(int fid) {
        return ruleMapper.delByFid(fid);
    }
}
