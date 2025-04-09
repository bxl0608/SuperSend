package com.send.dao.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.send.dao.repository.QuickReplyCustomerDao;
import com.send.model.db.mysql.QuickReplyCustomer;
import org.springframework.stereotype.Repository;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Repository
public interface QuickReplyCustomerDao4Mysql extends QuickReplyCustomerDao, BaseMapper<QuickReplyCustomer> {

}
