package com.send.model.auth;

import com.send.model.db.mysql.TbSysPermission;
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
@Data
public class UserDetail {
    private Integer id;
    private String username;
    private String nickName;
    private List<TbSysPermission> permissionList;
    private List<String> permissionURIList;
    /**
     * 角色名称列表
     */
    private List<String> roleList;

    /**
     * 密码更新时间：用于判断密码是否过期
     */
    private LocalDateTime passwordUpdateTime;

    /**
     * 用户是否过期
     */
    private Integer expireType;
    private LocalDateTime expireDate;
    /**
     * 是否已经成功登录过：首次登录判断标记
     */
    private Boolean loggedFlag;

    /**
     * 是否为内置用户
     */
    private Boolean builtinFlag;
    /**
     * 是否锁定
     */
    private Boolean enabled;
    /**
     * 密码是否过期
     */
    private Boolean passwordExpired;

}
