package com.send.admin.service.bo.user;

import com.project.base.model.pagination.PageRequest;
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
@ApiModel("用户分页查询参数")
@Data
public class PageQueryUserRequestBo {
    @ApiModelProperty("分页")
    private PageRequest page;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色名称")
    private String role;

    @ApiModelProperty("状态：-1=所有，0=正常，1=锁定，2=过期")
    private Integer status;
}