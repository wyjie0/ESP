package mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.DO.Server;

import java.util.List;

public interface ProvenanceMapper {
    @Insert("insert into t_provenance " +
            "values(default,#{fid},#{fileStage},#{blockHash},#{txHash},#{Provenance}," +
            "#{stateLength},#{PIDLength},#{blockHashLength},#{signatureLength})")
    int createPro(@Param("fid") int fid, @Param("fileStage") int fileStage,
                  @Param("blockHash") String blockHash, @Param("txHash") String txHash,
                  @Param("Provenance") String Provenance, @Param("stateLength") int stateLength,
                  @Param("PIDLength") int PIDLength, @Param("blockHashLength") int blockHashLength,
                  @Param("signatureLength") int signatureLength
    );

    @Select("select * from t_provenance " +
            "where fid = #{fid} " +
            "order by fileStage DESC " +
            "limit 1")
    Server selBlockHashPro(@Param("fid") int fid);

    @Select("select * from t_provenance where fid = #{fid}")
    List<Server> selAllByFid(@Param("fid") int fid);

    @Select("select count(*) from t_provenance where fid = #{fid}")
    int selCountByFid(@Param("fid") int fid);

    @Select("select txHash from t_provenance where fid = #{fid} and fileStage = #{fileStage}")
    String selTxHashByFidFileStage(@Param("fid") int fid, @Param("fileStage") int fileStage);

    @Select("select blockHash from t_provenance")
    List<String> selAllBlock();
}
