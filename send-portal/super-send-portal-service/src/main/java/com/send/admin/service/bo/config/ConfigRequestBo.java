package com.send.admin.service.bo.config;

import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class ConfigRequestBo {
    private String type;

    private Object detail;
}
