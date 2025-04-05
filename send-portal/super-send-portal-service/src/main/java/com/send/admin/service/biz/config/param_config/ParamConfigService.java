package com.send.admin.service.biz.config.param_config;

import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.param_config.impl.SystemTimeConfigService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.project.base.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class ParamConfigService {
    @Autowired
    private List<IConfigDetailService> configDetailServiceList;

    @Autowired
    private SystemTimeConfigService systemTimeConfigService;

    private IConfigDetailService findServiceByType(String type) {
        if (CollectionUtils.isEmpty(configDetailServiceList)) {
            return null;
        }
        for (IConfigDetailService iConfigDetailService : configDetailServiceList) {
            if (StringUtils.equalsIgnoreCase(iConfigDetailService.getType(), type)) {
                return iConfigDetailService;
            }
        }
        return null;
    }


    public int save(ConfigRequestBo bo) {
        // 校验
        if (StringUtils.isBlank(bo.getType())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_TYPE);
        }
        IConfigDetailService configDetailService = findServiceByType(bo.getType());
        if (configDetailService == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_TYPE);
        }
        return configDetailService.save(bo);
    }

    public TbConfigBo<IConfigDetail> findByType(String type) {
        // 校验
        if (StringUtils.isBlank(type)) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_TYPE);
        }
        IConfigDetailService configDetailService = findServiceByType(type);
        if (configDetailService == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_TYPE);
        }
        return configDetailService.findByType(type);
    }

    public <T extends IConfigDetail> TbConfigBo<T> findByDetailClass(Class<T> clazz) {
        for (IConfigDetailService configDetailService : configDetailServiceList) {
            if (configDetailService.getConfigDetailClass() == clazz) {
                return configDetailService.findByDetailClass(clazz);
            }
        }
        return null;
    }

    public void ntp(String ntpHostname) {
        systemTimeConfigService.ntp(ntpHostname);
    }
}
