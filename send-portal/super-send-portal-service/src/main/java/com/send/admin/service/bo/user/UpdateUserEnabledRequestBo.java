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
@ApiModel("用户更新锁定状态")
@Data
public class UpdateUserEnabledRequestBo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("账号启用禁用状态")
    private Boolean enabled;
}