package pojo.DO;

import it.unisa.dia.gas.jpbc.Element;

public class AuthenticationServer {
    public AuthenticationServer() {
    }

    private static Element sk_as;//会话密钥

    public static Element getSk_as() {
        return sk_as;
    }

    public static void setSk_as(Element sk_as) {
        AuthenticationServer.sk_as = sk_as;
    }
}
