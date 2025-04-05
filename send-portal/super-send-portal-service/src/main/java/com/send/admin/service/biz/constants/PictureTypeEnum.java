package com.send.admin.service.biz.constants;

import lombok.Getter;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public enum PictureTypeEnum {
    BACKGROUND("background"),
    LOGO_LOGIN("loginLogo"),
    LOGO_MAIN("mainLogo"),
    TITLE("icon");

    @Getter
    private final String type;

    PictureTypeEnum(String type) {
        this.type = type;
    }

    public static boolean contains(String type) {
        return findEnum(type) != null;
    }

    public static PictureTypeEnum findEnum(String type) {
        for (PictureTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
