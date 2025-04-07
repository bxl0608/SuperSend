package com.send.model.enums;

import lombok.Getter;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-07 20:27
 * @Description:1:系统充值,2:用户消费
 * @Company: Information Technology Company
 */
@Getter
public enum ChangeTypeEnum {

    /**
     * 1:系统充值
     */
    SYSTEM_TOP_UP(1, "系统充值"),
    /**
     * 2:用户消费
     */
    USER_CONSUMPTION(2, "用户消费");

    private final Integer type;
    private final String desc;

    ChangeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
