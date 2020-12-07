package pojo.DO;

import java.util.List;

public class Audit {
    private int fid;
    private int stageCount;
    private List<String> txData;

    public Audit() {
    }

    public Audit(int fid, int stageCount, List<String> txData) {
        this.fid = fid;
        this.stageCount = stageCount;
        this.txData = txData;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getStageCount() {
        return stageCount;
    }

    public void setStageCount(int stageCount) {
        this.stageCount = stageCount;
    }

    public List<String> getTxData() {
        return txData;
    }

    public void setTxData(List<String> txData) {
        this.txData = txData;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "fid=" + fid +
                ", stageCount=" + stageCount +
                ", txData=" + txData +
                '}';
    }
}
