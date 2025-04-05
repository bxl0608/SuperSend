package com.send.admin.service.bo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class RouterResponseBO {

    @ApiModelProperty("路由列表")
    private List<FrontRouter> routerList;

    @Data
    public static class FrontRouter {

        @ApiModelProperty("路由")
        private String router;

        @ApiModelProperty("菜单名称")
        private String menuName;

        @ApiModelProperty("菜单图标")
        private String icon;

        public List<FrontRouter> children;
    }
}
