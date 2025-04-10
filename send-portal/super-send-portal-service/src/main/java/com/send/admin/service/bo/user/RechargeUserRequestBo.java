package com.send.admin.service.bo.user;

import com.project.base.validation.annotation.Length;
import com.project.base.validation.annotation.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
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

    /**
     * 变动类型1:系统变动,2:用户消费
     */
    @NotNull(message = "变动类型不能为空")
    @Min(value = 1, message = "充值变动类型固定值1")
    @Max(value = 1, message = "充值变动类型固定值1")
    private Integer changeType;
    @NotNull(message = "变动金额不能为空")
    @ApiModelProperty("变动金额")
    @DecimalMin(value = "-9999999999.9999", inclusive = true, message = "变动金额必须大于等于-9999999999.9999")
    @DecimalMax(value = "9999999999.9999", inclusive = true, message = "变动金额必须小于等于9999999999.9999")
    private BigDecimal accountBalance;

    @ApiModelProperty("备注")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
}