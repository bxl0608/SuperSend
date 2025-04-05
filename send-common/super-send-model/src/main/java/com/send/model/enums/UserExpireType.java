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
public enum UserExpireType {

    /**
     * 永不过期
     */
    NEVER(0, "永不过期"),
    /**
     * 有时效性的
     */
    TEMPORARY(1, "有时效性的");

    private final Integer type;
    private final String desc;

    UserExpireType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
