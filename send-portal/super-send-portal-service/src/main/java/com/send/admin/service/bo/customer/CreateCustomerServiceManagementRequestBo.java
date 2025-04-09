package com.send.admin.service.bo.customer;

import com.project.base.validation.annotation.Length;
import com.project.base.validation.annotation.NotBlank;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-09 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class CreateCustomerServiceManagementRequestBo {

    /**
     * 客服用户名（必须为11位手机号）
     * 数据库字段：customer_name varchar(20) NOT NULL
     */
    @NotBlank(message = "客服用户名不能为空")
    @Length(max = 11, message = "客服用户名不能超过11个字符")
    private String customerName;


    /**
     * 备注信息
     * 数据库字段：remarks varchar(500) NOT NULL
     */
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

}