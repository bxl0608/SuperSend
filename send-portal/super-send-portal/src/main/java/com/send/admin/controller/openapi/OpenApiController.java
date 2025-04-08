package com.send.admin.controller.openapi;

import com.project.base.model.web.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月08日 20:23
 * @description：
 */
@Slf4j
@RestController
@RequestMapping("/openapi")
@Api(tags = "openapi模块")
public class OpenApiController {
    @PostMapping("/test")
    @ApiOperation(value = "测试")
    public CommonResponse<String> test() {
        return CommonResponse.builder("openapi模块").build();
    }
}
