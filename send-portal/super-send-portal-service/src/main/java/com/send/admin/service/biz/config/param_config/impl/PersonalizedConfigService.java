package com.send.admin.service.biz.config.param_config.impl;

import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.impl.PersonalizedConfigDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.param_config.AbstractConfigDetailService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.project.base.common.json.JsonTool;
import com.project.base.model.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class PersonalizedConfigService extends AbstractConfigDetailService {
    @Override
    public String getType() {
        return ConfigTypeConstant.PERSONALIZED_CONFIG;
    }

    @Override
    public Class<? extends IConfigDetail> getConfigDetailClass() {
        return PersonalizedConfigDetail.class;
    }

    @Override
    public int save(ConfigRequestBo bo) {
        PersonalizedConfigDetail detail = JsonTool.toObject(JsonTool.toJson(bo.getDetail()), PersonalizedConfigDetail.class);
        if (detail == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_DETAIL);
        }
        //
        if (StringUtils.isBlank(detail.getProductName())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_PRODUCT_NAME);
        }
        if (StringUtils.isBlank(detail.getLoginName())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_LOGIN_NAME);
        }
        if (StringUtils.isBlank(detail.getSystemName())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_SYSTEM_NAME);
        }
        if (StringUtils.isBlank(detail.getRightName())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_RIGHT_NAME);
        }
        // 保存 或 更新
        return buildAndSave(bo, detail);
    }
}
