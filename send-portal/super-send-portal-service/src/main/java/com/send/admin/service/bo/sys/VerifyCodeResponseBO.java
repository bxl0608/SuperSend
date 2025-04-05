package com.send.admin.service.bo.sys;

import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class VerifyCodeResponseBO {
    private String verifyCodeId;
    private String verifyCodeBase64;
    private String verifyCodePlain;
}
