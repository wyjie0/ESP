package pojo.DO;

public class Server {
    private int pid;
    private int fid;
    private int fileStage;
    private String blockHash;
    private String txHash;
    private String Provenance;
    private int stateLength;
    private int PIDLength;
    private int blockHashLength;
    private int signatureLength;

    public Server() {
    }

    public Server(int fid, int fileStage, String blockHash, String txHash, String provenance, int stateLength, int PIDLength, int blockHashLength, int signatureLength) {
        this.fid = fid;
        this.fileStage = fileStage;
        this.blockHash = blockHash;
        this.txHash = txHash;
        Provenance = provenance;
        this.stateLength = stateLength;
        this.PIDLength = PIDLength;
        this.blockHashLength = blockHashLength;
        this.signatureLength = signatureLength;
    }

    public Server(int pid, int fid, int fileStage, String blockHash, String txHash, String provenance, int stateLength, int PIDLength, int blockHashLength, int signatureLength) {
        this.pid = pid;
        this.fid = fid;
        this.fileStage = fileStage;
        this.blockHash = blockHash;
        this.txHash = txHash;
        Provenance = provenance;
        this.stateLength = stateLength;
        this.PIDLength = PIDLength;
        this.blockHashLength = blockHashLength;
        this.signatureLength = signatureLength;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getFileStage() {
        return fileStage;
    }

    public void setFileStage(int fileStage) {
        this.fileStage = fileStage;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getProvenance() {
        return Provenance;
    }

    public void setProvenance(String provenance) {
        Provenance = provenance;
    }

    public int getStateLength() {
        return stateLength;
    }

    public void setStateLength(int stateLength) {
        this.stateLength = stateLength;
    }

    public int getPIDLength() {
        return PIDLength;
    }

    public void setPIDLength(int PIDLength) {
        this.PIDLength = PIDLength;
    }

    public int getBlockHashLength() {
        return blockHashLength;
    }

    public void setBlockHashLength(int blockHashLength) {
        this.blockHashLength = blockHashLength;
    }

    public int getSignatureLength() {
        return signatureLength;
    }

    public void setSignatureLength(int signatureLength) {
        this.signatureLength = signatureLength;
    }

    @Override
    public String toString() {
        return "Server{" +
                "fid=" + fid +
                ", fileStage=" + fileStage +
                ", blockHash='" + blockHash + '\'' +
                ", txHash='" + txHash + '\'' +
                ", Provenance='" + Provenance + '\'' +
                ", stateLength=" + stateLength +
                ", PIDLength=" + PIDLength +
                ", blockHashLength=" + blockHashLength +
                ", signatureLength=" + signatureLength +
                '}';
    }
}
