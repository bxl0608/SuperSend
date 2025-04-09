package com.send.admin.service.bo.customer;

import com.project.base.validation.annotation.Length;
import com.project.base.validation.annotation.NotBlank;
import com.project.base.validation.annotation.NotNull;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-09 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class UpdateCustomerServiceManagementRequestBo {

    @NotNull(message = "素材id不能为空")
    private String id;


    /**
     * 备注信息
     * 数据库字段：remarks varchar(500) NOT NULL
     */
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

}