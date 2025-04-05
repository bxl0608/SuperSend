package com.send.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public class PasswordTool {
    /**
     * 生成盐
     *
     * @return 盐值
     */
    public static String genSalt() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }

    /**
     * hash 盐:密码
     *
     * @param salt     盐
     * @param password 密码
     * @return hash码
     * @throws NoSuchAlgorithmException 异常
     */
    public static String hash(String salt, String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        String hashContent = salt + ":" + password;
        byte[] digestByteArray = md.digest(hashContent.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digestByteArray);
    }


}
