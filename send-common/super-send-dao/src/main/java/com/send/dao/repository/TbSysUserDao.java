package com.send.dao.repository;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.send.model.db.mysql.TbSysUser;
import com.send.model.db.mysql.bo.PageQueryUserBo;
import com.send.model.db.mysql.vo.PageQueryUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public interface TbSysUserDao extends BaseMapper<TbSysUser> {
    List<PageQueryUserBo> page(@Param("input") PageQueryUserVo vo);

    Integer count(@Param("input") PageQueryUserVo vo);
}
