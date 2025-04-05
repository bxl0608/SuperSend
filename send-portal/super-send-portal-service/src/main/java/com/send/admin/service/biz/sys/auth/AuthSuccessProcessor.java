package com.send.admin.service.biz.sys.auth;

import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.db.mysql.bo.config.impl.LoginConfigDetail;
import com.send.admin.service.biz.config.ParamConfigCacheService;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.biz.sys.TokenService;
import com.send.admin.service.biz.sys.auth.event.AuthEvent;
import com.send.admin.service.biz.config.ConfigEvent;
import com.send.model.auth.UserDetail;
import com.project.base.common.thread.ThreadContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Component
public class AuthSuccessProcessor implements ApplicationListener<AuthEvent> {

    @Autowired
    private ParamConfigCacheService paramConfigCacheService;
    @Autowired
    private TokenService tokenService;

    @Override
    public void onApplicationEvent(AuthEvent event) {
        if (event.getAuthResult() == AuthEvent.AuthResultEnum.LOGIN_FAIL) {
            return;
        }

        UserDetail userDetail = event.getUserDetail();
        String token = event.getToken();

        ThreadContext.put(AuthService.KEY_USER_ID, userDetail.getId());
        ThreadContext.put(AuthService.KEY_USER_NAME, userDetail.getUsername());
        ThreadContext.put(AuthService.KEY_USER_DETAIL, userDetail);
        ThreadContext.put(AuthService.KEY_TOKEN, token);

        if (event.getAuthResult() == AuthEvent.AuthResultEnum.LOGIN_SUCCESS) {
            // 判断登录模式，若为true，则允许同一账号同时登录（多token），否则移除用户其他的token
            if (!findLoginModeFromDb()) {
                tokenService.deleteTokenByUserDetailButTheGivenOne(userDetail.getId(), token);
            }
        }
    }

    private boolean findLoginModeFromDb() {
        Boolean loginMode = paramConfigCacheService.getLoginConfigDetail().getLoginMode();
        return loginMode == null || loginMode;
    }

    private boolean findLoginMode(TbConfigBo<IConfigDetail> loginConfig) {
        if (loginConfig == null || loginConfig.getDetail() == null) {
            return ParamConfigCacheService.DEFAULT_LOGIN_MODE;
        }
        if (loginConfig.getDetail() instanceof LoginConfigDetail) {
            LoginConfigDetail detail = (LoginConfigDetail) loginConfig.getDetail();
            if (detail.getLoginMode() == null) {
                return ParamConfigCacheService.DEFAULT_LOGIN_MODE;
            }
            return detail.getLoginMode();
        }
        return ParamConfigCacheService.DEFAULT_LOGIN_MODE;
    }

    /**
     * 当登录模式发生变化时，判断是否需要移除当前用户的其他token
     */
    @EventListener(value = ConfigEvent.class)
    public void listenLoginModeChange(ConfigEvent configEvent) {
        if (configEvent == null || configEvent.getNewBo() == null) {
            return;
        }
        if (!ConfigTypeConstant.LOGIN_CONFIG.equals(configEvent.getNewBo().getType())) {
            return;
        }

        boolean oldLoginMode = findLoginMode(configEvent.getOldBo());
        boolean newLoginMode = findLoginMode(configEvent.getNewBo());
        if (oldLoginMode == newLoginMode) {
            return;
        }
        if (!newLoginMode) {
            Integer userId = ObjectUtils.defaultIfNull(configEvent.getCurrentLoginUserId(), ThreadContext.get(AuthService.KEY_USER_ID));
            String token = ObjectUtils.defaultIfNull(configEvent.getCurrentToken(), ThreadContext.get(AuthService.KEY_TOKEN));
            tokenService.deleteTokenByUserDetailButTheGivenOne(userId, token);
        }
    }
}
