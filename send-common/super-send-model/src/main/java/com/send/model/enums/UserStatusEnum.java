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
public enum UserStatusEnum {
    /**
     * 所有
     */
    ALL(-1, "所有"),
    /**
     * 正常
     */
    NORMAL(0, "正常"),
    /**
     * 锁定
     */
    LOCKED(1, "锁定"),
    /**
     * 过期
     */
    EXPIRE(2, "过期");

    private final Integer status;
    private final String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
