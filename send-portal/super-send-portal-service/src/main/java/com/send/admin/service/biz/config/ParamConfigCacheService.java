package com.send.admin.service.biz.config;

import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.db.mysql.bo.config.impl.CreditConfigDetail;
import com.send.model.db.mysql.bo.config.impl.LoginConfigDetail;
import com.send.model.db.mysql.bo.config.impl.PasswordConfigDetail;
import com.send.model.db.mysql.bo.config.impl.SystemTimeConfigDetail;
import com.send.admin.service.biz.config.param_config.impl.SystemTimeConfigService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class ParamConfigCacheService implements InitializingBean {

    public static final boolean DEFAULT_LOGIN_MODE = true;
    /**
     * 默认锁定时间：5分钟
     */
    public static final int DEFAULT_LOCK_DURATION = 5;
    /**
     * 默认会话时间：120分钟
     */
    public static final int DEFAULT_SESSION_DURATION = 120;
    /**
     * 默认登录失败次数：5
     */
    public static final int DEFAULT_LOGIN_FAIL_LIMIT = 5;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SystemTimeConfigService systemTimeConfigService;

    @Getter
    private PasswordConfigDetail passwordConfigDetail;

    @Getter
    private CreditConfigDetail creditConfigDetail;

    @Getter
    private SystemTimeConfigDetail systemTimeConfigDetail;

    @Getter
    private LoginConfigDetail loginConfigDetail;

    @Override
    public void afterPropertiesSet() throws Exception {
        passwordConfigDetail = initPasswordConfig();
        creditConfigDetail = initCreditConfig();
        systemTimeConfigDetail = initSystemTimeConfig();
        loginConfigDetail = initLoginConfigDetail();
    }

    private LoginConfigDetail initLoginConfigDetail() {
        TbConfigBo<LoginConfigDetail> loginConfig = configService.findLoginConfig();
        LoginConfigDetail detail;
        if (loginConfig != null && loginConfig.getDetail() != null) {
            detail = loginConfig.getDetail();
        } else {
            detail = new LoginConfigDetail();
        }
        loginConfigDetailDefaultValueSetting(detail);
        return detail;
    }

    private void loginConfigDetailDefaultValueSetting(LoginConfigDetail detail) {
        detail.setLoginMode(ObjectUtils.defaultIfNull(detail.getLoginMode(), DEFAULT_LOGIN_MODE));
        detail.setLoginFailLimit(ObjectUtils.defaultIfNull(detail.getLoginFailLimit(), DEFAULT_LOGIN_FAIL_LIMIT));
        detail.setLockDuration(ObjectUtils.defaultIfNull(detail.getLockDuration(), DEFAULT_LOCK_DURATION));
        detail.setSessionDuration(ObjectUtils.defaultIfNull(detail.getSessionDuration(), DEFAULT_SESSION_DURATION));
    }

    private PasswordConfigDetail initPasswordConfig() {
        TbConfigBo<PasswordConfigDetail> config = configService.findPasswordConfig();
        if (config != null && config.getDetail() != null) {
            return config.getDetail();
        }
        PasswordConfigDetail detail = new PasswordConfigDetail();
        detail.setMinLength(8);
        detail.setNumEnable(true);
        detail.setCapitalEnable(false);
        detail.setCharacterEnable(true);
        detail.setExpireEnable(false);
        detail.setExpireDuration(90);
        return detail;
    }

    private CreditConfigDetail initCreditConfig() {
        TbConfigBo<CreditConfigDetail> config = configService.findCreditConfig();
        if (config != null && config.getDetail() != null) {
            return config.getDetail();
        }
        CreditConfigDetail detail = new CreditConfigDetail();
        detail.setCreditSwitch(false);
        detail.setCreditIps(Collections.emptyList());
        return detail;
    }

    private SystemTimeConfigDetail initSystemTimeConfig() {
        TbConfigBo<SystemTimeConfigDetail> config = configService.findSystemTimeConfig();
        if (config != null && config.getDetail() != null) {
            return config.getDetail();
        }
        SystemTimeConfigDetail detail = new SystemTimeConfigDetail();
        detail.setNetworkEnable(false);
        detail.setNtpServerAddress(null);
        return detail;
    }

    /**
     * 当锁定时间发生变化时，重建缓存
     */
    @EventListener(value = ConfigEvent.class)
    public void listenConfigChange(ConfigEvent configEvent) {
        if (configEvent == null || configEvent.getNewBo() == null) {
            return;
        }

        if (ConfigTypeConstant.PASSWORD_CONFIG.equals(configEvent.getNewBo().getType())) {
            TbConfigBo<IConfigDetail> newBo = configEvent.getNewBo();
            if (!(newBo.getDetail() instanceof PasswordConfigDetail)) {
                passwordConfigDetail = initPasswordConfig();
            } else {
                passwordConfigDetail = (PasswordConfigDetail) newBo.getDetail();
            }
            return;
        }

        if (ConfigTypeConstant.CREDIT_CONFIG.equals(configEvent.getNewBo().getType())) {
            TbConfigBo<IConfigDetail> newBo = configEvent.getNewBo();
            if (!(newBo.getDetail() instanceof CreditConfigDetail)) {
                creditConfigDetail = initCreditConfig();
            } else {
                creditConfigDetail = (CreditConfigDetail) newBo.getDetail();
            }
            return;
        }

        if (ConfigTypeConstant.SYSTEM_TIME_CONFIG.equals(configEvent.getNewBo().getType())) {
            TbConfigBo<IConfigDetail> newBo = configEvent.getNewBo();
            if (!(newBo.getDetail() instanceof SystemTimeConfigDetail)) {
                systemTimeConfigDetail = initSystemTimeConfig();
            } else {
                systemTimeConfigDetail = (SystemTimeConfigDetail) newBo.getDetail();
            }
            if (BooleanUtils.isNotTrue(systemTimeConfigDetail.getNetworkEnable())) {
                systemTimeConfigService.ntpStop();
            }
            return;
        }

        if (ConfigTypeConstant.LOGIN_CONFIG.equals(configEvent.getNewBo().getType())) {
            TbConfigBo<IConfigDetail> newBo = configEvent.getNewBo();
            if (!(newBo.getDetail() instanceof LoginConfigDetail)) {
                loginConfigDetail = initLoginConfigDetail();
            } else {
                LoginConfigDetail detail = (LoginConfigDetail) newBo.getDetail();
                loginConfigDetailDefaultValueSetting(detail);
                loginConfigDetail = detail;
            }
            return;
        }

    }
}
