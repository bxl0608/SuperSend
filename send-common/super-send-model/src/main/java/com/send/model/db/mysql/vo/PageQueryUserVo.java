package com.send.model.db.mysql.vo;

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
public class PageQueryUserVo {
    private Integer from;

    private Integer offset;

    private String username;
    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 状态：-1=所有，0=正常，1=锁定，2=过期
     */
    private Integer status;

    @ApiModelProperty("账号启用禁用状态")
    private Integer enabled;

    /**
     * 登录失败被锁定的用户列表
     */
    private List<String> lockedUsernames;
}
