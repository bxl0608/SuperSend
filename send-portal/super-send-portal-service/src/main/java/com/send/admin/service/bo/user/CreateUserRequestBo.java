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
public class CreateUserRequestBo {
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色列表")
    private List<String> roleNameList;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("昵称或者全名")
    private String nickname;

    @ApiModelProperty("0-永久，1-临时")
    private Integer expireType;

    @ApiModelProperty("有效期")
    private LocalDateTime expireDate;
}