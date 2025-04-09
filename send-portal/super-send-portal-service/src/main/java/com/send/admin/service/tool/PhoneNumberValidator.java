package com.send.admin.service.tool;

import java.util.regex.Pattern;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月09日 15:04
 * @description：
 */
public class PhoneNumberValidator {
    //校验手机号码是否为数字
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^\\d{11}$");
    }

    // 正则表达式：匹配11位数字，且以常见手机号段开头
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(13[0-9]|14[5-9]|15[0-3,5-9]|16[2,5-7]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$"
    );

    //严格校验手机号码
    public static boolean isStrictValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}