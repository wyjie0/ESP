import java.text.SimpleDateFormat;

public class TestDate {
    public static String Date() {
        // 13λ���뼶���ʱ���
        long time2 = 1564145766895L;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time2);
    }
}
