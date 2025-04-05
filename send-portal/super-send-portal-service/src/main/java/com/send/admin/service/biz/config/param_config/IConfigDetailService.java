package com.send.admin.service.biz.config.param_config;


import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.admin.service.bo.config.ConfigRequestBo;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public interface IConfigDetailService {

    /**
     * 类型
     *
     * @return 类型
     */
    String getType();

    /**
     * 获取详情对应的class
     *
     * @return class
     */
    Class<? extends IConfigDetail> getConfigDetailClass();

    /**
     * 保存
     *
     * @param bo 详情
     * @return 保存数量
     */
    int save(ConfigRequestBo bo);

    /**
     * 查询
     *
     * @param type 类型
     * @return 配置
     */
    TbConfigBo<IConfigDetail> findByType(String type);

    /**
     * 查询
     *
     * @param detailClass 类型class
     * @return 配置
     */
    <T extends IConfigDetail> TbConfigBo<T> findByDetailClass(Class<T> detailClass);
}
