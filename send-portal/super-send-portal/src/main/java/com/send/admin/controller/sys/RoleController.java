package com.send.admin.controller.sys;

import com.project.base.model.pagination.PageList;
import com.project.base.model.web.CommonResponse;
import com.send.admin.service.biz.role.RoleService;
import com.send.admin.service.biz.user.UserService;
import com.send.admin.service.bo.role.RoleResponseBo;
import com.send.admin.service.bo.user.PageQueryUserResponseBo;
import com.send.admin.vo.user.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-06 17:29
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@RestController
@RequestMapping("/role")
@Api(tags = "用户模块")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/list")
    @ApiOperation(value = "查询角色列表")
    public CommonResponse<List<RoleResponseBo>> list() {
        List<RoleResponseBo> responseBo = roleService.list();
        return CommonResponse.builder(responseBo).build();
    }

}
