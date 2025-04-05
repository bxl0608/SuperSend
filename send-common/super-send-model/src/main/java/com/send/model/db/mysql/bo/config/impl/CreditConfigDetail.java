package com.send.model.db.mysql.bo.config.impl;

import com.send.model.db.mysql.bo.config.IConfigDetail;
import lombok.Data;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class CreditConfigDetail implements IConfigDetail {
    /**
     * 授信开关
     */
    private Boolean creditSwitch;
    /**
     * 授信IP
     */
    private List<String> creditIps;
}
