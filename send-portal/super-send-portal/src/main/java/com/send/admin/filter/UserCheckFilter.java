package com.send.admin.filter;

import com.send.model.auth.UserDetail;
import com.send.model.enums.UserExpireType;
import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.biz.sys.auth.RetryCountProcessor;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import com.project.base.web.filter.AuthApiAnonymousFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Component
@Slf4j
@Order(3)
public class UserCheckFilter implements Filter {
    @Value("#{'${auth.validateUpdatePwd.exclude:/portal/user/updatePwd}'.split('\\s*,\\s*')}")
    private Set<String> validateUpdatePwdSkipPaths;

    @Autowired
    private RetryCountProcessor retryCountProcessor;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);

        /* 如果允许匿名访问，则直接访问 */
        boolean anonymousEnabled = AuthApiAnonymousFilter.anonymousEnabled(servletRequest);
        if (anonymousEnabled) {
            filterChain.doFilter(request, wrapper);
            return;
        }

        // 判断用户是否被锁定
        validateUserLock();
        // 判断用户是否过期
        validateUserExpire();

        // 判断是否需要修改密码
        validatePasswordExpire(request.getRequestURI());

        /* 继续处理其他filter */
        filterChain.doFilter(request, wrapper);
    }

    private void validateUserLock() {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        retryCountProcessor.validateUserLock(userDetail.getUsername());
    }

    private void validateUserExpire() {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        if (UserExpireType.TEMPORARY.getType().equals(userDetail.getExpireType()) &&
                userDetail.getExpireDate() != null &&
                userDetail.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(MasterExceptionEnum.ERR_USER_EXPIRE);
        }
    }

    /**
     * 是否需要更新密码
     *
     * @param requestUri url
     */
    private void validatePasswordExpire(String requestUri) {
        for (String validateUpdatePwdSkipPath : validateUpdatePwdSkipPaths) {
            if (antPathMatcher.match(validateUpdatePwdSkipPath, requestUri)) {
                return;
            }
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        // 首次登录，需要改密
        if (BooleanUtils.isNotTrue(userDetail.getLoggedFlag())) {
            throw new BusinessException(MasterExceptionEnum.ERR_FIRST_LOGIN_NEED_CHANGE_PASSWORD);
        }
        // 密码过期后，需要改密
        if (BooleanUtils.isTrue(userDetail.getPasswordExpired())) {
            throw new BusinessException(MasterExceptionEnum.ERR_PASSWORD_EXPIRE);
        }
    }
}
