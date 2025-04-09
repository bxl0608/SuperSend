package com.send.model.enums;

import lombok.Getter;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Getter
public enum CustomerServiceManagementEnum {
    /**
     * 离线
     */
    OFFLINE(1, "离线"),
    /**
     * 在线
     */
    ONLINE(2, "在线");

    private final Integer status;
    private final String desc;

    CustomerServiceManagementEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
