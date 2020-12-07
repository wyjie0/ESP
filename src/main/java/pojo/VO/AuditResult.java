package pojo.VO;

public class AuditResult {
    private boolean checkPn;
    private boolean checkSt_n;
    private boolean checkAmount;
    private boolean checkRestPn;

    public AuditResult() {
    }

    public AuditResult(boolean checkPn, boolean checkSt_n, boolean checkAmount, boolean checkRestPn) {
        this.checkPn = checkPn;
        this.checkSt_n = checkSt_n;
        this.checkAmount = checkAmount;
        this.checkRestPn = checkRestPn;
    }

    public boolean isCheckPn() {
        return checkPn;
    }

    public void setCheckPn(boolean checkPn) {
        this.checkPn = checkPn;
    }

    public boolean isCheckSt_n() {
        return checkSt_n;
    }

    public void setCheckSt_n(boolean checkSt_n) {
        this.checkSt_n = checkSt_n;
    }

    public boolean isCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(boolean checkAmount) {
        this.checkAmount = checkAmount;
    }

    public boolean isCheckRestPn() {
        return checkRestPn;
    }

    public void setCheckRestPn(boolean checkRestPn) {
        this.checkRestPn = checkRestPn;
    }

    @Override
    public String toString() {
        return "AuditResult{" +
                "checkPn=" + checkPn +
                ", checkSt_n=" + checkSt_n +
                ", checkAmount=" + checkAmount +
                ", checkRestPn=" + checkRestPn +
                '}';
    }
}
