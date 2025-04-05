package com.send.model.enums;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public enum PermissionTypeEnum {
    ROUTER(1),
    URI(2);

    private final int value;

    PermissionTypeEnum(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
