package com.send.admin.controller;

import com.project.base.model.pagination.PageList;
import com.project.base.model.web.CommonResponse;
import com.send.admin.service.biz.material.CustomerServiceManagementService;
import com.send.admin.service.bo.customer.CreateCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.customer.DeleteCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.customer.PageQueryCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.customer.UpdateCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.material.CreateMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.DeleteMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.UpdateMaterialLibraryRequestBo;
import com.send.model.db.mysql.CustomerServiceManagement;
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
@RequestMapping("/customerServiceManagement")
@Api(tags = "客服管理模块")
public class CustomerServiceManagementController {

    @Autowired
    private CustomerServiceManagementService customerServiceManagementService;

    @PostMapping("/list")
    @ApiOperation(value = "客服管理列表")
    public CommonResponse<PageList<CustomerServiceManagement>> list(@RequestBody PageQueryCustomerServiceManagementRequestBo vo) {
        PageList<CustomerServiceManagement> responseBo = customerServiceManagementService.pageList(vo);
        return CommonResponse.builder(responseBo).build();
    }

    @PostMapping("/create")
    @ApiOperation(value = "客服管理创建")
    public CommonResponse<Integer> create(@Validated @RequestBody CreateCustomerServiceManagementRequestBo vo) {
        int count = customerServiceManagementService.create(vo);
        return CommonResponse.builder(count).build();
    }


    @PostMapping("/update")
    @ApiOperation(value = "客服管理更新")
    public CommonResponse<Integer> update(@Validated @RequestBody UpdateCustomerServiceManagementRequestBo vo) {
        int count = customerServiceManagementService.update(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "客服管理删除")
    public CommonResponse<Integer> delete(@Validated @RequestBody DeleteCustomerServiceManagementRequestBo vo) {
        int count = customerServiceManagementService.delete(vo);
        return CommonResponse.builder(count).build();
    }
}
