package com.send.admin.service.tool;

import com.send.model.exception.MasterExceptionEnum;
import com.project.base.common.codec.RsaTool;
import com.project.base.model.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class RsaLocalTool {
    private static String privateKey;
    private static String publicKey;

    static {
        init();
    }

    @SneakyThrows
    private static void init() {
        Resource resource = new ClassPathResource("privateRSA.key");
        privateKey = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        resource = new ClassPathResource("publicRSA.key");
        publicKey = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    public static String privateDecrypt(String encryptStrByPublicKey) {
        try {
            return RsaTool.privateDecrypt(encryptStrByPublicKey, privateKey);
        } catch (Exception e) {
            throw new BusinessException(MasterExceptionEnum.ERR_DECRYPT, "");
        }
    }

    public static String publicEncrypt(String plain) {
        return RsaTool.publicEncrypt(plain, publicKey);
    }

    public static String getPublicKey() {
        return publicKey;
    }
}
