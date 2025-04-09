package com.whatsapp;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.NumberParseException;

/**
 * 将手机号码解析为国际区号和国内区号
 */
public class PhoneNumberParse {
    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public static PhoneInfo parse(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // 预处理电话号码
        String processedNumber = preprocessPhoneNumber(phoneNumber);
        if (processedNumber == null) {
            return null;
        }

        PhoneInfo info = new PhoneInfo();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(processedNumber, null);
            info.setCc(String.valueOf(number.getCountryCode()));
            info.setIn(String.valueOf(number.getNationalNumber()));
        } catch (NumberParseException e) {
            return null;
        }
        return info;
    }

    /**
     * 预处理电话号码
     * 1. 移除所有空格和特殊字符
     * 2. 如果号码以0开头，移除前导0
     * 3. 如果号码不以+开头，添加+
     */
    private static String preprocessPhoneNumber(String phoneNumber) {
        // 移除所有空格和特殊字符
        String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");
        
        // 如果号码为空，返回null
        if (cleanNumber.isEmpty()) {
            return null;
        }

        // 如果号码以+开头，直接返回
        if (cleanNumber.startsWith("+")) {
            return cleanNumber;
        }

        // 如果号码以0开头，移除前导0
        if (cleanNumber.startsWith("0")) {
            cleanNumber = cleanNumber.substring(1);
        }

        // 添加+号
        return "+" + cleanNumber;
    }

    public static class PhoneInfo {
        private String cc; // 国际区号
        private String in; // 国内区号

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }

        public String getIn() {
            return in;
        }

        public void setIn(String in) {
            this.in = in;
        }
    }
}
