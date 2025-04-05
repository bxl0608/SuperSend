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
    ADMIN(1, "admin"),
    /**
     * 审计员
     */
    AUDIT(2, "audit"),
    /**
     * 普通用户
     */
    COMMON(3, "common");

    private final Integer type;
    private final String name;

    UserRoleEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
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
