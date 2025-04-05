package com.send.admin.service.bo.user;

import io.swagger.annotations.ApiModel;
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
@ApiModel("更新用户")
@Data
public class UpdateUserRequestBo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("角色列表")
    private List<String> roleNameList;

    @ApiModelProperty("昵称或者全名")
    private String nickname;

    @ApiModelProperty("0-永久，1-临时")
    private Integer expireType;

    @ApiModelProperty("有效期")
    private LocalDateTime expireDate;
}