package pojo.DO;

import java.util.List;

public class Rule {
    private int uid;
    private int fid;
    private String operate;
    private List<File> file;

    public Rule() {
    }

    public Rule(int uid, int fid) {
        this.uid = uid;
        this.fid = fid;
    }

    public Rule(int uid, int fid, String operate) {
        this.uid = uid;
        this.fid = fid;
        this.operate = operate;
    }

    public Rule(int uid, int fid, String operate, List<File> file) {
        this.uid = uid;
        this.fid = fid;
        this.operate = operate;
        this.file = file;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public List<File> getFile() {
        return file;
    }

    public void setFile(List<File> file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "uid=" + uid +
                ", fid=" + fid +
                ", operate='" + operate + '\'' +
                '}';
    }
}
