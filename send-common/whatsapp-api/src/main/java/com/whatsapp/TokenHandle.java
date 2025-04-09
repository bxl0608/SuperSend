package com.whatsapp;

import cn.hutool.crypto.digest.MD5;

import java.nio.charset.StandardCharsets;

class TokenHandle {

    public static String iosToken(String phone, String version, boolean isBusiness) {
        String prefix;
        if (isBusiness) {
            prefix = getVipPrefix(version);
        } else {
            prefix = getPrefix(version);
        }

        return MD5.create().digestHex((prefix + phone).getBytes(StandardCharsets.UTF_8));
    }

    private static String getPrefix(String version) {
        return "0a1mLfGUIBVrMKF1RdvLI5lkRBvof6vn0fD2QRSM" + MD5.create().digestHex(version).toLowerCase();
    }

    private static String getVipPrefix(String version) {
        return "USUDuDYDeQhY4RF2fCSp5m3F6kJ1M2J8wS7bbNA2" + MD5.create().digestHex(version).toLowerCase();
    }
}
