package pojo.DTO;

import pojo.DO.File;

public class FileRight extends File {
    private String operateDateString;//修改类型
    private String newOperatePID;//新的PID
    private boolean own;
    private boolean read;
    private boolean write;
    private String txHash;

    public FileRight(int fid, int fileStage, String fileState, String fileName, String fileType, double fileSize,
                     long operateDate, String operatePID, byte[] file, String filePath, String txData,
                     String operateDateString, String newOperatePID) {
        super(fid, fileStage, fileState, fileName, fileType, fileSize, operateDate, operatePID, file, filePath, txData);
        this.operateDateString = operateDateString;
        this.newOperatePID = newOperatePID;
    }

    public String getOperateDateString() {
        return operateDateString;
    }

    public void setOperateDateString(String operateDateString) {
        this.operateDateString = operateDateString;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getNewOperatePID() {
        return newOperatePID;
    }

    public void setNewOperatePID(String newOperatePID) {
        this.newOperatePID = newOperatePID;
    }

    @Override
    public String toString() {
        return super.toString() + "FileRight{" +
                "operateDateString='" + operateDateString + '\'' +
                ", own=" + own +
                ", read=" + read +
                ", write=" + write +
                ", txHash='" + txHash + '\'' +
                ", newOperatePID='" + newOperatePID + '\'' +
                '}';
    }
}
