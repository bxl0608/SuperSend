package com.send.admin.service.bo.user;

import com.project.base.validation.annotation.NotNull;
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
    @NotNull(message = "用户id不能为空")
    private Integer id;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("账号启用禁用状态")
    private Boolean enabled;
}