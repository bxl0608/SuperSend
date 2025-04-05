package com.send.admin.service.biz.config.param_config.impl;

import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.impl.LoginConfigDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.param_config.AbstractConfigDetailService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.project.base.common.json.JsonTool;
import com.project.base.model.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class LoginConfigService extends AbstractConfigDetailService {
    @Override
    public String getType() {
        return ConfigTypeConstant.LOGIN_CONFIG;
    }

    @Override
    public Class<? extends IConfigDetail> getConfigDetailClass() {
        return LoginConfigDetail.class;
    }

    @Override
    public int save(ConfigRequestBo bo) {
        LoginConfigDetail detail = JsonTool.toObject(JsonTool.toJson(bo.getDetail()), LoginConfigDetail.class);
        if (detail == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_DETAIL);
        }
        // 具体校验
        // 连续登录失败最大次数
        Integer loginFailLimit = detail.getLoginFailLimit();
        if (loginFailLimit == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_LOGIN_FAIL_LIMIT);
        }
        if (loginFailLimit < 2 || loginFailLimit > 10) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_LOGIN_FAIL_LIMIT);
        }
        // 锁定时间
        Integer lockDuration = detail.getLockDuration();
        if (lockDuration == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_LOCK_DURATION);
        }
        if (lockDuration < 1 || lockDuration > 14400) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_LOCK_DURATION);
        }
        // 会话超时时间
        Integer sessionDuration = detail.getSessionDuration();
        if (sessionDuration == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_SESSION_DURATION);
        }
        if (sessionDuration < 0 || sessionDuration > 120) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_SESSION_DURATION);
        }
        // 登录策略
        if (detail.getLoginMode() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_LOGIN_MODE);
        }

        // 保存 或 更新
        return buildAndSave(bo, detail);
    }

}
