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
@ApiModel("删除用户")
@Data
public class DeleteUserRequestBo {

    @ApiModelProperty("id")
    private Integer id;
}