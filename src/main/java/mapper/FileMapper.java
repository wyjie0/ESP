package mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.DO.File;

import java.util.List;

public interface FileMapper {
    /**
     * 存入首次创建文件记录信息
     */
    @Insert("insert into t_file " +
            "values (#{fid},1,'create',#{fileName},#{fileType},#{fileSize},#{operateDate},#{operatePID},#{file},#{filePath})")
    int createFile(@Param("fid") int fid,
                   @Param("fileName") String fileName,
                   @Param("fileType") String fileType,
                   @Param("fileSize") double fileSize,
                   @Param("operateDate") long operateDate,
                   @Param("operatePID") String operatePID,
                   @Param("file") byte[] file,
                   @Param("filePath") String filePath);

    /**
     * 存入之后的文件记录信息
     */
    @Insert("insert into t_file " +
            "values (#{fid},#{fileStage},#{fileState},#{fileName},#{fileType},#{fileSize},#{operateDate},#{operatePID},#{file},#{filePath})")
    int editFile(@Param("fid") int fid,
                 @Param("fileStage") int fileStage,
                 @Param("fileState") String fileState,
                 @Param("fileName") String fileName,
                 @Param("fileType") String fileType,
                 @Param("fileSize") double fileSize,
                 @Param("operateDate") long operateDate,
                 @Param("operatePID") String operatePID,
                 @Param("file") byte[] file,
                 @Param("filePath") String filePath);

    /**
     * 存入文件删除信息
     */
    @Insert("insert into t_file (fid,fileStage,fileState,fileName,operateDate,operatePID) " +
            "values (#{fid},#{fileStage},'destroy','destroyed',#{operateDate},#{operatePID})")
    int deleteFile(@Param("fid") int fid,
                   @Param("fileStage") int fileStage,
                   @Param("operateDate") long operateDate,
                   @Param("operatePID") String operatePID);

    /**
     * 找出fid对应文件最大阶段号
     */
    @Select("select ifnull(max(fileStage),0) " +
            "from t_file " +
            "where fid=#{fid}")
    int selMaxStage(@Param("fid") int fid);

    /**
     * 找到fid文件的所有记录
     */
    @Select("select fid,fileStage,fileState,fileName,fileType,fileSize,operateDate,operatePID " +
            "from t_file " +
            "where fid=#{fid}")
    List<File> selFileByFid(@Param("fid") int fid);

    @Select("select ifnull(max(fid),0) " +
            "from t_file")
    int selMaxFid();

    @Select("select * " +
            "from t_file " +
            "where fid=#{fid} and fileStage=#{fileStage}")
    File selFile(@Param("fid") int fid, @Param("fileStage") int fileStage);

    @Select("select filePath " +
            "from t_file " +
            "where fid=#{fid} and fileStage=#{fileStage}")
    String selFilePathByFidAndFileStage(@Param("fid") int fid, @Param("fileStage") int fileStage);

    /**
     * 注意不能先分组，否则会丢失组内顺序信息
     * 先根据fileStage排序，再根据fid分组，再根据operateDate排序
     */
    @Select("select * " +
            "from (select fid,fileStage,fileState,fileName,fileType,fileSize,operateDate,operatePID " +
            "      from (select fid,fileStage,fileState,fileName,fileType,fileSize,operateDate,operatePID" +
            "            from t_file " +
            "            order by fileStage desc " +
            "            limit 999999) a " +
            "      group by fid) b " +
            "      order by operateDate desc")
    List<File> selAllFileGroupByUid();

    /**
     * 选出与我相关的所有文件
     */
    @Select("select fid,fileStage,fileState,fileName,fileType,fileSize,operateDate,operatePID " +
            "from (select fid,fileStage,fileState,fileName,fileType,fileSize,operateDate,operatePID " +
            "      from (select fid,fileStage,fileState,fileName,fileType,fileSize,operateDate,operatePID" +
            "            from (select * " +
            "                  from t_file " +
            "                  where fid in (select fid " +
            "                                from t_rule " +
            "                                where uid=#{uid})) c" +
            "            order by fileStage desc " +
            "            limit 999999) a " +
            "      group by fid) b " +
            "order by operateDate desc")
    List<File> selMyFileGroupByUid(@Param("uid") int uid);
}
