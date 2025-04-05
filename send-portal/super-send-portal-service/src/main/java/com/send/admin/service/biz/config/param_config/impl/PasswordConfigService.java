package com.send.admin.service.biz.config.param_config.impl;

import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.impl.PasswordConfigDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.param_config.AbstractConfigDetailService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.project.base.common.json.JsonTool;
import com.project.base.model.exception.BusinessException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class PasswordConfigService extends AbstractConfigDetailService {
    @Override
    public String getType() {
        return ConfigTypeConstant.PASSWORD_CONFIG;
    }

    @Override
    public Class<? extends IConfigDetail> getConfigDetailClass() {
        return PasswordConfigDetail.class;
    }

    @Override
    public int save(ConfigRequestBo bo) {
        PasswordConfigDetail detail = JsonTool.toObject(JsonTool.toJson(bo.getDetail()), PasswordConfigDetail.class);
        if (detail == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_DETAIL);
        }
        // 具体校验
        Integer minLength = detail.getMinLength();
        if (minLength == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_MIN_LENGTH);
        }
        if (minLength < 8 || minLength > 16) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_MIN_LENGTH);
        }
        // 字符种类，复杂度校验
        Boolean numEnable = detail.getNumEnable();
        if (numEnable == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_NUM_ENABLE);
        }
        Boolean capitalEnable = detail.getCapitalEnable();
        if (capitalEnable == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_CAPITAL_ENABLE);
        }
        Boolean characterEnable = detail.getCharacterEnable();
        if (characterEnable == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_CHARACTER_ENABLE);
        }
        int enabledCount = 0;
        if (BooleanUtils.isTrue(numEnable)) {
            enabledCount++;
        }
        if (BooleanUtils.isTrue(capitalEnable)) {
            enabledCount++;
        }
        if (BooleanUtils.isTrue(characterEnable)) {
            enabledCount++;
        }
        if (enabledCount < 2) {
            throw new BusinessException(MasterExceptionEnum.CONFIG_PASSWORD_COMPLEXITY);
        }
        // 密码有效期相关字段校验
        Boolean expireEnable = detail.getExpireEnable();
        if (expireEnable == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_EXPIRE_ENABLE);
        }
        Integer expireDuration = detail.getExpireDuration();
        if (BooleanUtils.isTrue(expireEnable) && expireDuration == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_EXPIRE_DURATION);
        }
        if (expireDuration != null && (expireDuration < 5 || expireDuration > 3000)) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_EXPIRE_DURATION);
        }

        // 保存 或 更新
        return buildAndSave(bo, detail);
    }

}
