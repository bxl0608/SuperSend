package com.send.admin.service.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    private List<String> roleNameList;

    @ApiModelProperty("昵称或者全名")
    private String nickname;

    @ApiModelProperty("有效期")
    private LocalDateTime expireDate;

    @ApiModelProperty("是否过期")
    private Boolean expired;

    @ApiModelProperty("是否被锁定")
    private Boolean locked;

    @ApiModelProperty("false-非内置用户，true-内置用户")
    private Boolean builtinFlag;

}