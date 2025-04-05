package com.send.admin.service.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@ApiModel("用户更新自身密码")
@Data
public class UpdateUserPwdRequestBo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户旧密码")
    private String oldPassword;

    @ApiModelProperty("用户新密码")
    private String newPassword;
}