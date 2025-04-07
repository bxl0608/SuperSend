package com.send.admin.controller.sys;

import com.send.admin.service.biz.user.UserService;
import com.send.admin.service.bo.user.PageQueryUserResponseBo;
import com.send.admin.vo.user.*;
import com.project.base.model.pagination.PageList;
import com.project.base.model.web.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询用户")
    public CommonResponse<PageList<PageQueryUserResponseBo>> list(@RequestBody PageQueryUserRequestVo vo) {
        PageList<PageQueryUserResponseBo> responseBo = userService.list(vo);
        return CommonResponse.builder(responseBo).build();
    }


    @PostMapping("/create")
    @ApiOperation(value = "创建用户")
    public CommonResponse<Integer> create(@RequestBody CreateUserRequestVo vo) {
        int count = userService.create(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/recharge")
    @ApiOperation(value = "充值")
    public CommonResponse<BigDecimal> recharge(@RequestBody RechargeUserRequestVo vo) {
        BigDecimal count = userService.recharge(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新用户")
    public CommonResponse<Integer> update(@RequestBody UpdateUserRequestVo vo) {
        int count = userService.update(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/enable")
    @ApiOperation(value = "管理员激活或封锁用户")
    public CommonResponse<Integer> updateEnabled(@RequestBody UpdateUserEnabledRequestVo vo) {
        int count = userService.updateEnabledByAdmin(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/updatePwd")
    @ApiOperation(value = "用户更新自身密码")
    public CommonResponse<Integer> updatePwd(@RequestBody UpdateUserPwdRequestVo vo) {
        int count = userService.updatePwd(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/resetPwd")
    @ApiOperation(value = "管理员重置用户密码")
    public CommonResponse<Integer> resetPwd(@RequestBody ResetUserPwdRequestVo vo) {
        int count = userService.resetPwd(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    public CommonResponse<Integer> delete(@RequestBody DeleteUserRequestVo vo) {
        int count = userService.delete(vo);
        return CommonResponse.builder(count).build();
    }
}
