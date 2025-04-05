package com.send.admin.service.biz.config.param_config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.send.dao.repository.TbConfigDao;
import com.send.model.db.mysql.TbConfig;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.ConfigEvent;
import com.send.admin.service.biz.config.event.ConfigEventPublisher;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.send.admin.service.biz.sys.AuthService;
import com.project.base.common.json.JsonTool;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public abstract class AbstractConfigDetailService implements IConfigDetailService {
    protected static final String LAST_SQL_LIMIT_1 = " limit 1";
    @Resource
    protected TbConfigDao tbConfigDao;
    @Autowired
    protected ConfigEventPublisher configEventPublisher;

    protected TbConfig buildTbConfig(ConfigRequestBo bo, IConfigDetail iConfigDetail) {
        LocalDateTime now = LocalDateTime.now();
        TbConfig tbConfig = new TbConfig();
        tbConfig.setType(bo.getType());
        tbConfig.setDetail(JsonTool.toJson(iConfigDetail));
        tbConfig.setCreateTime(now);
        tbConfig.setUpdateTime(now);
        return tbConfig;
    }

    protected int buildAndSave(ConfigRequestBo bo, IConfigDetail iConfigDetail) {
        TbConfig tbConfig = buildTbConfig(bo, iConfigDetail);

        TbConfig oldOne = tbConfigDao.selectOne(Wrappers.lambdaQuery(TbConfig.class).eq(TbConfig::getType, bo.getType()).last(LAST_SQL_LIMIT_1));

        int count;
        if (oldOne != null) {
            tbConfig.setId(oldOne.getId());
            tbConfig.setCreateTime(null);
            count = tbConfigDao.updateById(tbConfig);
        } else {
            count = tbConfigDao.insert(tbConfig);
        }
        if (count > 0) {
            afterSave(tbConfig, iConfigDetail, oldOne, getConfigDetailClass());
        }
        return count;
    }

    /**
     * 事后处理逻辑
     *
     * @param tbConfig          新配置
     * @param newDetail         新配置详情
     * @param oldOne            旧配置
     * @param configDetailClass 具体class
     */
    protected void afterSave(TbConfig tbConfig, IConfigDetail newDetail, TbConfig oldOne, Class<? extends IConfigDetail> configDetailClass) {
        IConfigDetail oldDetail = oldOne == null ? null : JsonTool.toObject(oldOne.getDetail(), configDetailClass);
        ConfigEvent configEvent = new ConfigEvent(this);
        configEvent.setNewBo(buildTbConfigBo(tbConfig, newDetail));
        configEvent.setOldBo(buildTbConfigBo(oldOne, oldDetail));
        configEvent.setCurrentToken(ThreadContext.get(AuthService.KEY_TOKEN));
        configEvent.setCurrentLoginUserId(ThreadContext.get(AuthService.KEY_USER_ID));
        configEventPublisher.publishConfigEvent(configEvent);
    }

    private TbConfigBo<IConfigDetail> buildTbConfigBo(TbConfig tbConfig, IConfigDetail detail) {
        if (tbConfig == null) {
            return null;
        }
        TbConfigBo<IConfigDetail> newBo = new TbConfigBo<>();
        newBo.setId(tbConfig.getId());
        newBo.setDetail(detail);
        newBo.setType(tbConfig.getType());
        newBo.setCreateTime(tbConfig.getCreateTime());
        newBo.setUpdateTime(tbConfig.getUpdateTime());
        return newBo;
    }

    @Override
    public TbConfigBo<IConfigDetail> findByType(String type) {
        // 校验
        TbConfig oldOne = getTbConfig(type);
        if (oldOne == null) {
            return null;
        }
        TbConfigBo<IConfigDetail> bo = new TbConfigBo<>();
        bo.setId(oldOne.getId());
        bo.setType(oldOne.getType());
        bo.setCreateTime(oldOne.getCreateTime());
        bo.setUpdateTime(oldOne.getUpdateTime());
        if (StringUtils.isNotBlank(oldOne.getDetail())) {
            IConfigDetail iConfigDetail = JsonTool.toObject(oldOne.getDetail(), getConfigDetailClass());
            bo.setDetail(iConfigDetail);
        }
        return bo;
    }

    @Override
    public <T extends IConfigDetail> TbConfigBo<T> findByDetailClass(Class<T> clazz) {
        // 判断detail类型是否正确
        if (clazz != getConfigDetailClass()) {
            return null;
        }
        TbConfig oldOne = getTbConfig(getType());
        if (oldOne == null) {
            return null;
        }

        TbConfigBo<T> bo = new TbConfigBo<>();
        bo.setId(oldOne.getId());
        bo.setType(oldOne.getType());
        bo.setCreateTime(oldOne.getCreateTime());
        bo.setUpdateTime(oldOne.getUpdateTime());
        if (StringUtils.isNotBlank(oldOne.getDetail())) {
            T iConfigDetail = JsonTool.toObject(oldOne.getDetail(), clazz);
            bo.setDetail(iConfigDetail);
        }
        return bo;
    }

    private TbConfig getTbConfig(String type) {
        // 校验
        if (StringUtils.isBlank(type)) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_TYPE);
        }
        Class<? extends IConfigDetail> configDetailClass = getConfigDetailClass();
        if (configDetailClass == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_TYPE);
        }

        return tbConfigDao.selectOne(Wrappers.lambdaQuery(TbConfig.class).eq(TbConfig::getType, type).last(LAST_SQL_LIMIT_1));
    }
}
