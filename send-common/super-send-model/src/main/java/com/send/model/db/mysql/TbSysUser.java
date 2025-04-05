package com.send.model.db.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.send.model.enums.UserExpireType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
@FieldNameConstants
public class TbSysUser implements Serializable {

    private static final long serialVersionUID = -3590075703752838600L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 密码盐
     */
    private String passwordSalt;

    /**
     * 昵称或者全名
     */
    private String nickname;

    /**
     * 0-永久，1-临时
     *
     * @see UserExpireType
     */
    private Integer expireType;

    /**
     * 有效期
     */
    private LocalDateTime expireDate;

    /**
     * 密码更新时间
     */
    private LocalDateTime passwordUpdateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 账号启用禁用状态
     */
    private Boolean enabled;

    /**
     * 0-非内置用户，1-内置用户
     */
    private Boolean builtinFlag;

    /**
     * 0-已登录，1-未登录过：用于首次登录进行改密
     */
    private Boolean loggedFlag;

    /**
     * 删除符 已删除-0，未删除-1
     */
    private Boolean deleteFlag;
}