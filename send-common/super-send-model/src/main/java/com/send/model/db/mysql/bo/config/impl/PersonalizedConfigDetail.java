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
public class PersonalizedConfigDetail implements IConfigDetail {
    /**
     * 登录页名称
     */
    private String loginName;
    /**
     * 主页系统名
     */
    private String systemName;
    /**
     * 页底Copyright
     */
    private String rightName;

    /**
     * 授权页版本名
     */
    private String productName;
}
