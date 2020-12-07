package pojo.DO;

import java.util.Arrays;

public class File {
    protected int fid;
    protected int fileStage;
    protected String fileState;//创建、修改、删除、转让
    protected String fileName;
    protected String fileType;
    protected double fileSize;
    protected long operateDate;
    //private int createUid;
    //private String createUname;
    protected String operatePID;
    protected byte[] file;
    protected String filePath;
    //区块链交易的数据字段
    protected String txData;

    public File() {
    }

    public File(int fid, int fileStage, String fileState, String fileName, String fileType, double fileSize, long operateDate, String operatePID, byte[] file, String filePath, String txData) {
        this.fid = fid;
        this.fileStage = fileStage;
        this.fileState = fileState;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.operateDate = operateDate;
        this.operatePID = operatePID;
        this.file = file;
        this.filePath = filePath;
        this.txData = txData;
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

    public String getFileState() {
        return fileState;
    }

    public void setFileState(String fileState) {
        this.fileState = fileState;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public long getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(long operateDate) {
        this.operateDate = operateDate;
    }

    public String getOperatePID() {
        return operatePID;
    }

    public void setOperatePID(String operatePID) {
        this.operatePID = operatePID;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTxData() {
        return txData;
    }

    public void setTxData(String txData) {
        this.txData = txData;
    }

    @Override
    public String toString() {
        return "File{" +
                "fid=" + fid +
                ", fileStage=" + fileStage +
                ", fileState=" + fileState +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", operateDate=" + operateDate +
                ", operatePID='" + operatePID + '\'' +
                ", file=" + Arrays.toString(file) +
                ", filePath='" + filePath + '\'' +
                ", txData='" + txData + '\'' +
                '}';
    }
}
