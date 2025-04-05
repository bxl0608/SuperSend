package com.send.admin.filter;

import com.send.model.auth.UserDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.sys.TokenService;
import com.send.admin.service.biz.sys.auth.event.AuthEvent;
import com.send.admin.service.biz.sys.auth.event.AuthEventPublisher;
import com.project.base.model.exception.BusinessException;
import com.project.base.web.filter.AuthApiAnonymousFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */

@Component
@Slf4j
@Order(2)
public class AuthApiCheckFilter implements Filter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthEventPublisher authEventPublisher;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);


        log.debug("*** filter 2 : start execute ...");

        /* 如果允许匿名访问，则直接访问 */
        boolean anonymousEnabled = AuthApiAnonymousFilter.anonymousEnabled(servletRequest);
        if (anonymousEnabled) {
            filterChain.doFilter(request, wrapper);
            return;
        }

        /* 验证token */
        String token = TokenService.getAuthorizationFromRequest(request);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }

        /* 通过token 获取用户信息 */
        UserDetail userDetail = tokenService.getUserDetail(token);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }

        /* 通过用户信息 获取api权限列表 */
        List<String> permissionList = userDetail.getPermissionURIList();
        if (CollectionUtils.isEmpty(permissionList)) {
            throw new BusinessException(MasterExceptionEnum.ERR_FORBIDDEN);
        }

        /* 验证接口权限 */
        String requestUri = request.getRequestURI();
        boolean matched = false;
        for (String permissionUri : permissionList) {
            matched = antPathMatcher.match(permissionUri, requestUri);
            if (matched) {
                log.debug("*** filter 2 : Url Access: {}", requestUri);
                break;
            }
        }

        if (matched) {
            /* 发布授权成功事件 */
            AuthEvent authEvent = new AuthEvent(this);
            authEvent.setAuthResult(AuthEvent.AuthResultEnum.TOKEN_SUCCESS);
            authEvent.setUserDetail(userDetail);
            authEvent.setToken(token);
            authEvent.setUsername(userDetail.getUsername());
            authEventPublisher.publishAuthEvent(authEvent);

            /* 继续处理其他filter */
            filterChain.doFilter(request, wrapper);
        } else {
            throw new BusinessException(MasterExceptionEnum.ERR_FORBIDDEN);
        }
    }
}
