package com.send.admin.vo.sys;

import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class ConfigResponseVo<T> {
    private T detail;
    private String type;

    @Data
    public static class Title {
        private String loginName;
        private String productName;
        private String rightName;
        private String systemName;
    }
}
