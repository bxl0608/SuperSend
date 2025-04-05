package com.send.model.db.mysql.bo.config.impl;

import com.send.model.db.mysql.bo.config.IConfigDetail;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class LoginConfigDetail implements IConfigDetail {
    /**
     * 登录失败次数
     */
    private Integer loginFailLimit;
    /**
     * 锁定时间
     */
    private Integer lockDuration;
    /**
     * 会话失效时间
     */
    private Integer sessionDuration;
    /**
     * 登录模式 true-多地同用户登录，false-单点登录
     */
    private Boolean loginMode;
}
