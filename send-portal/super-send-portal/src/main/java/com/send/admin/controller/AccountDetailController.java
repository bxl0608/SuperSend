package com.send.admin.controller;

import com.project.base.model.pagination.PageList;
import com.project.base.model.web.CommonResponse;
import com.send.admin.service.biz.AccountDetailService;
import com.send.admin.service.bo.PageQueryAccountDetailRequestBo;
import com.send.admin.service.bo.user.PageQueryUserResponseBo;
import com.send.admin.vo.user.PageQueryUserRequestVo;
import com.send.model.db.mysql.AccountDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月07日 20:45
 * @description：
 */
@Slf4j
@RestController
@RequestMapping("/account/detail")
@Api(tags = "财务管理模块")
public class AccountDetailController {

    @Autowired
    private AccountDetailService accountDetailService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询财务明细")
    public CommonResponse<PageList<AccountDetail>> list(@RequestBody PageQueryAccountDetailRequestBo vo) {
        PageList<AccountDetail> responseBo = accountDetailService.pageList(vo);
        return CommonResponse.builder(responseBo).build();
    }
}
