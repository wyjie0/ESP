package mapper;

import org.apache.ibatis.annotations.*;
import pojo.DO.Rule;

import java.util.List;

public interface RuleMapper {
    @Insert("insert into t_rule values(#{uid},#{fid},#{operate})")
    int insRule(@Param("uid") int uid, @Param("fid") int fid, @Param("operate") String operate);

    @Select("select ifnull(operate,'') from t_rule where uid = #{uid} and fid = #{fid}")
    List<String> selRule(@Param("uid") int uid, @Param("fid") int fid);

    @Select("select ifnull(operate,'') from t_rule where uid = #{uid} and fid = #{fid}")
    List<String> selRuleList(@Param("uid") int uid, @Param("fid") int fid);

    @Update("update t_rule set operate = #{operate} where uid = #{uid} and fid = #{fid}")
    int updRule(@Param("uid") int uid, @Param("fid") int fid, @Param("operate") String operate);

    @Delete("delete from t_rule where fid = #{fid}")
    int delByFid(@Param("fid") int fid);

    /**
     * <resultMap id="RoleFileMap" type="rule">
     * <id column="uid" property="uid"/>
     * <id column="fid" property="fid"/>
     * <id column="operate" property="operate"/>
     * <collection property="file" ofType="file"
     * select="mapper.FileMapper.selFileByFid" column="fid"/>
     * </resultMap>
     * <select id="selAll" parameterType="int" resultMap="RoleFileMap">
     * select * from t_rule where uid=#{uid}
     * </select>
     */
    @Results(value = {
            @Result(id = true, property = "uid", column = "uid"),
            @Result(property = "fid", column = "fid"),
            @Result(property = "operate", column = "operate"),
            @Result(property = "file", many = @Many(select = "mapper.FileMapper.selFileByFid"), column = "fid")
    })
    @Select("select * from t_rule where uid = #{uid}")
    List<Rule> selAll(@Param("uid") int uid);

    @Select("select ifnull(fid,0) from t_rule where uid = #{uid} and operate = 'own'")
    List<Integer> selOwnByUid(@Param("uid") int uid);
}
