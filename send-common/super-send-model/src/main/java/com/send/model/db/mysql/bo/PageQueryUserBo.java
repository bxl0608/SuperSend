package com.send.model.db.mysql.bo;

import com.send.model.db.mysql.TbSysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryUserBo extends TbSysUser {
    /**
     * 角色列表
     */
    private String roleIds;
}