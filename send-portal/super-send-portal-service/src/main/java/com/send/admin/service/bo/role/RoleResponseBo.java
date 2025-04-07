package com.send.admin.service.bo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("角色")
@Data
public class RoleResponseBo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("角色类型")
    private String roleType;
    @ApiModelProperty("角色名称")
    private String roleName;


}