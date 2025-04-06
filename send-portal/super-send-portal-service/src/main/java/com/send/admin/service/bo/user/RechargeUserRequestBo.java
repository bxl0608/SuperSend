package com.send.admin.service.bo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    @ApiModelProperty("充值用户id")
    private Integer id;
    @ApiModelProperty("用户余额")
    private Integer accountBalance;
}