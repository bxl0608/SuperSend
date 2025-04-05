package com.send.dao.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.send.dao.repository.TbConfigDao;
import com.send.model.db.mysql.TbConfig;
import org.springframework.stereotype.Repository;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Repository
public interface TbConfigDao4Mysql extends TbConfigDao, BaseMapper<TbConfig> {

}
