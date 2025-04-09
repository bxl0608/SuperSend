package com.whatsapp;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class TestCheck {
    public static void main(String[] args) {
        /*
         * 如果是线上环境，不需要代理，在本地测试需要代理
         */
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 7890));
//        String phone = "819058951353";
//        String phone = "12179960000";
        String phone = "8616688889999";
        String version = "2.25.10.72";

        WhatsappAccountStatus whatsappAccountStatus = WhatsappChecker.checkBlock(phone, version, proxy);
        System.out.println(whatsappAccountStatus);
    }
}
