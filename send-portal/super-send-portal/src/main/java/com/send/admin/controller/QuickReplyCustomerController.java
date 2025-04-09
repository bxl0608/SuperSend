package com.send.admin.controller;

import com.project.base.model.pagination.PageList;
import com.project.base.model.web.CommonResponse;
import com.send.admin.service.biz.material.QuickReplyCustomerService;
import com.send.admin.service.bo.reply.CreateQuickReplyCustomerRequestBo;
import com.send.admin.service.bo.reply.DeleteQuickReplyCustomerRequestBo;
import com.send.admin.service.bo.reply.PageQueryQuickReplyCustomerRequestBo;
import com.send.admin.service.bo.reply.UpdateQuickReplyCustomerRequestBo;
import com.send.model.db.mysql.QuickReplyCustomer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/quickReplyCustomer")
@Api(tags = "客服快捷回复")
public class QuickReplyCustomerController {

    @Autowired
    private QuickReplyCustomerService quickReplyCustomerService;

    @PostMapping("/list")
    @ApiOperation(value = "客服快捷回复列表")
    public CommonResponse<PageList<QuickReplyCustomer>> list(@RequestBody PageQueryQuickReplyCustomerRequestBo vo) {
        PageList<QuickReplyCustomer> responseBo = quickReplyCustomerService.pageList(vo);
        return CommonResponse.builder(responseBo).build();
    }

    @PostMapping("/create")
    @ApiOperation(value = "客服快捷回复创建")
    public CommonResponse<Integer> create(@Validated @RequestBody CreateQuickReplyCustomerRequestBo vo) {
        int count = quickReplyCustomerService.create(vo);
        return CommonResponse.builder(count).build();
    }


    @PostMapping("/update")
    @ApiOperation(value = "客服快捷回复更新")
    public CommonResponse<Integer> update(@Validated @RequestBody UpdateQuickReplyCustomerRequestBo vo) {
        int count = quickReplyCustomerService.update(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "客服快捷回复删除")
    public CommonResponse<Integer> delete(@Validated @RequestBody DeleteQuickReplyCustomerRequestBo vo) {
        int count = quickReplyCustomerService.delete(vo);
        return CommonResponse.builder(count).build();
    }
}
