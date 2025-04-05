package com.send.common.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class TypeConvertTool {
    private TypeConvertTool() {
    }

    /**
     * string convert to integer
     *
     * @param intString 入参
     * @return 出参
     */
    public static Integer stringToInteger(String intString) {
        if (StringUtils.isBlank(intString)) {
            return null;
        }
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            log.warn("str to int failed...", e);
            return null;
        }
    }

    /**
     * string convert to long
     *
     * @param longString 入参
     * @return 出参
     */
    public static Long stringToLong(String longString) {
        if (StringUtils.isBlank(longString)) {
            return null;
        }
        try {
            return Long.parseLong(longString);
        } catch (NumberFormatException e) {
            log.warn("str to long failed...", e);
            return null;
        }
    }
}
