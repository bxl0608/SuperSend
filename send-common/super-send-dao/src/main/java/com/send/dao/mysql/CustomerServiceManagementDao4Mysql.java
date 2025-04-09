package com.send.dao.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.send.dao.repository.AccountDetailDao;
import com.send.dao.repository.CustomerServiceManagementDao;
import com.send.model.db.mysql.AccountDetail;
import com.send.model.db.mysql.CustomerServiceManagement;
import org.springframework.stereotype.Repository;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Repository
public interface CustomerServiceManagementDao4Mysql extends CustomerServiceManagementDao, BaseMapper<CustomerServiceManagement> {

}
