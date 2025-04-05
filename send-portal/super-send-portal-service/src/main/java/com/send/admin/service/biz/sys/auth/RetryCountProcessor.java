package com.send.admin.service.biz.sys.auth;

import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.config.ParamConfigCacheService;
import com.send.admin.service.biz.sys.LoginRetryCountService;
import com.send.admin.service.biz.sys.auth.event.AuthEvent;
import com.send.admin.service.biz.user.UserService;
import com.send.admin.service.bo.sys.LoginRequestBO;
import com.project.base.model.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Component
public class RetryCountProcessor implements ApplicationListener<AuthEvent> {

    @Autowired
    private LoginRetryCountService loginRetryCountService;
    @Autowired
    private UserService userService;

    @Autowired
    private ParamConfigCacheService paramConfigCacheService;

    public void process(LoginRequestBO loginRequestBO) {
        String username = loginRequestBO.getUsername();
        // 登录次数达到限制被锁定 或 被管理员主动锁定
        validateUserLock(username);
    }

    public void validateUserLock(String username) {
        int maxFailLoginCount = paramConfigCacheService.getLoginConfigDetail().getLoginFailLimit();
        int lockDuration = paramConfigCacheService.getLoginConfigDetail().getLockDuration();
        int count = loginRetryCountService.get(username);
        if (count >= maxFailLoginCount) {
            throw new BusinessException(MasterExceptionEnum.RETRY_COUNT_EXCEED, lockDuration);
        } else {
            boolean enabled = userService.isEnabled(username);
            if (!enabled) {
                throw new BusinessException(MasterExceptionEnum.LOCKED);
            }
        }
    }

    /**
     * 监听登录事件
     *
     * @param event 事件
     */
    @Override
    public void onApplicationEvent(AuthEvent event) {
        /* 清除重试次数 */
        if (event.getAuthResult() == AuthEvent.AuthResultEnum.LOGIN_SUCCESS) {
            if (StringUtils.isNotBlank(event.getUsername())) {
                if (loginRetryCountService.get(event.getUsername()) > 0) {
                    loginRetryCountService.clear(event.getUsername());
                } else {
                    userService.updateEnabledAfterLoginSuccess(event.getUsername());
                }
            }
            return;
        }

        /* 记录重试次数 */
        if (event.getAuthResult() == AuthEvent.AuthResultEnum.LOGIN_FAIL) {
            if (StringUtils.isNotBlank(event.getUsername())) {
                loginRetryCountService.incrementAndGet(event.getUsername());
            }
            return;
        }

    }
}
