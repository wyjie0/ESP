package service.Impl;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import mapper.ParamsMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import service.SysParamService;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

@Service
public class SysParamServiceImpl implements SysParamService {
    @Resource
    public ParamsMapper paramsMapper;

    public static BigInteger p;//群G和Gt的素数阶
    public static Field G;//群G
    public static Field Gt;//群Gt
    public static Element P;//群G的生成元
    public static Element P_pub;
    public static Element s;//私钥对
    public static Element k;//私钥对

    public static Pairing pairing;

    /**
     * 如果数据库中有系统参数，取出后直接赋值给本类，否则才需要初始化参数，后写入数据库（同缓存）
     */
    @Override
    public void init() {
        Logger logger = Logger.getLogger(SysParamServiceImpl.class);

        TypeACurveGenerator pg = new TypeACurveGenerator(512, 512);
        PairingParameters typeAParams = pg.generate();
        pairing = PairingFactory.getPairing(typeAParams);

        String strP = paramsMapper.selP();
        String strK = paramsMapper.selk();
        String strP_pub = paramsMapper.selP_pub();
        String strS = paramsMapper.sels();
        if (strP == null) {
            //重新创建并写入数据库
            logger.warn("系统初始化参数，重新创建并写入数据库");
            p = pairing.getG1().getOrder();
            G = pairing.getG1();
            Gt = pairing.getGT();
            P = pairing.getG1().newRandomElement().getImmutable();
            s = pairing.getZr().newRandomElement().getImmutable();
            k = pairing.getZr().newElement().setFromHash(s.toBytes(), 0, s.toBytes().length);
            P_pub = P.duplicate().mulZn(s.duplicate()).getImmutable();
            //Element转String
            byte[] byteP = P.toBytes();
            byte[] byteP_pub = P_pub.toBytes();
            byte[] bytes = s.toBytes();
            byte[] bytesk = k.toBytes();

            try {
                paramsMapper.insParams(
                        new String(byteP, "ISO8859-1"),
                        new String(byteP_pub, "ISO8859-1"),
                        new String(bytes, "ISO8859-1"),
                        new String(bytesk, "ISO8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            //已经存在系统参数
            logger.warn("系统初始化参数，已经存在系统参数");
            //String转Element
            try {
                P = pairing.getG1().newElementFromBytes(strP.getBytes("ISO8859-1"));
                k = pairing.getZr().newElementFromBytes(strK.getBytes("ISO8859-1"));
                P_pub = pairing.getG1().newElementFromBytes(strP_pub.getBytes("ISO8859-1"));
                s = pairing.getZr().newElementFromBytes(strS.getBytes("ISO8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        logger.info("AS的公钥为：" + P_pub.toString().substring(0, 15));
    }
}
