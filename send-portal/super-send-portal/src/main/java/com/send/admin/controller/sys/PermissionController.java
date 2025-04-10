package com.send.admin.controller.sys;

import com.send.admin.service.biz.sys.PermissionService;
import com.project.base.model.web.CommonResponse;
import com.send.admin.service.bo.sys.RouterResponseBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Api(tags = "菜单模块")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/listRouter")
    @ApiOperation(value = "路由列表")
    public CommonResponse<RouterResponseBO> listRouter() {
        RouterResponseBO routerResponseBO = permissionService.listRouter();
        return CommonResponse.builder(routerResponseBO).build();
    }

}
