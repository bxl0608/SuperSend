package com.send.dao.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.send.dao.repository.CustomerServiceManagementDao;
import com.send.dao.repository.MaterialLibraryDao;
import com.send.model.db.mysql.CustomerServiceManagement;
import com.send.model.db.mysql.MaterialLibrary;
import org.springframework.stereotype.Repository;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Repository
public interface MaterialLibraryDao4Mysql extends MaterialLibraryDao, BaseMapper<MaterialLibrary> {

}
