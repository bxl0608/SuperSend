package com.send.admin.service.bo.customer;

import com.project.base.validation.annotation.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-09 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@ApiModel("删除素材")
@Data
public class DeleteCustomerServiceManagementRequestBo {

    @ApiModelProperty("id")
    @NotNull(message = "客服id不能为空")
    private Integer id;
}