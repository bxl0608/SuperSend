package com.send.admin.service.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel("分页查询用户的响应")
@Data
public class PageQueryUserResponseBo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色名称列表")
    private String roleType;

    @ApiModelProperty("昵称或者全名")
    private String nickname;

    /**
     * 账户余额
     */
    private BigDecimal accountBalance;

    /**
     * 账号启用禁用状态,1-正常，0-封禁
     */
    private Boolean enabled;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private Boolean builtinFlag;
}