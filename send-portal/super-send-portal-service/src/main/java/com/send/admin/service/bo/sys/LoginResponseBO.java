package com.send.admin.service.bo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data

public class LoginResponseBO {

    @ApiModelProperty("是否内置")
    private Boolean builtinFlag;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("角色列表")
    private List<String> roleList;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("token")
    private String token;
}
