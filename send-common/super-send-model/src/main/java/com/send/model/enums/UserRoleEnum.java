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
public enum UserRoleEnum {
    /**
     * 管理员
     */
    ADMIN(1, "admin","管理员"),
    /**
     * 审计员
     */
    AUDIT(2, "audit","审计员"),
    /**
     * 普通用户
     */
    COMMON(3, "common","普通用户");

    private final Integer type;
    private final String name;
    private final String value;

    UserRoleEnum(Integer type, String name,String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public static Integer getTypeByName(String name) {
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.name.equals(name)) {
                return userRoleEnum.getType();
            }
        }
        return null;
    }
}
