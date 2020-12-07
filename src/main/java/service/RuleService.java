package service;

import pojo.DO.Rule;

import java.util.List;

public interface RuleService {
    /**
     * 根据uid fid operate更新规则
     */
    int updFileRule(Rule rule);

    /**
     * 根据uid fid operate插入规则
     */
    int insFileRule(Rule rule);

    /**
     * 根据uid fid查找规则集operate
     */
    List<String> selFileRule(Rule rule);

    /**
     * 根据uname fid插入规则operate
     */
    int insFileRule(String uname, int fid, String operate);

    /**
     * 根据fid删除规则
     */
    int delByFid(int fid);
}
