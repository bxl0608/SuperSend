package com.send.model.db.mysql.bo.config.impl;

import com.send.model.db.mysql.bo.config.IConfigDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordConfigDetail implements IConfigDetail {
    /**
     * 密码最小长度
     */
    private Integer minLength;
    /**
     * 密码复杂度支持数字
     */
    private Boolean numEnable;
    /**
     * 密码复杂度支持大写字母
     */
    private Boolean capitalEnable;
    /**
     * 密码复杂度支持特殊字符
     */
    private Boolean characterEnable;
    /**
     * 密码定时过期
     */
    private Boolean expireEnable;
    /**
     * 密码过期时长
     */
    private Integer expireDuration;
}
