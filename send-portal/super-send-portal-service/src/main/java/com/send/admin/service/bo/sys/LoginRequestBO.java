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
public class LoginRequestBO {
    private String username;
    private String password;
    private String verifyCode;
    private String verifyCodeId;
}
