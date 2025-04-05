package com.send.dao.repository;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.send.model.db.mysql.TbSysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public interface TbSysRoleDao extends BaseMapper<TbSysRole> {
    /**
     * 基于用户id查询角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<TbSysRole> findByUserId(@Param(value = "userId") Integer userId);
}
