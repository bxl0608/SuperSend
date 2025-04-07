package com.send.admin.service.bo.user;

import com.project.base.validation.annotation.Length;
import com.project.base.validation.annotation.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class RechargeUserRequestBo {

    @NotNull(message = "充值用户不能为空")
    @ApiModelProperty("充值用户id")
    private Integer id;
    @NotNull(message = "充值金额不能为空")
    @ApiModelProperty("充值金额")
    @DecimalMin(value = "0.0000", inclusive = false, message = "充值金额必须大于0")
    private BigDecimal accountBalance;

    @ApiModelProperty("备注")
    @Length(max = 500, message = "字段长度不能超过500个字符")
    private String remarks;
}