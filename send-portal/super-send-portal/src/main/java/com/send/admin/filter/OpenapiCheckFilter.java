package com.send.admin.filter;

import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import com.project.base.web.filter.AuthApiAnonymousFilter;
import com.send.admin.config.OpenApiConfig;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.biz.sys.auth.RetryCountProcessor;
import com.send.model.auth.UserDetail;
import com.send.model.enums.UserExpireType;
import com.send.model.exception.MasterExceptionEnum;
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
@Order(-1)
public class OpenapiCheckFilter implements Filter {
    public static final String BYPASS_ATTRIBUTE = "bypassAuth";
    private AntPathMatcher pathMatcher = new AntPathMatcher();
    @Autowired
    private OpenApiConfig openApiConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String token = httpRequest.getHeader("Authorization");
        // 检查是否是特殊token
        if (openApiConfig.getOpenapiToken().equals(token)) {
            String requestURI = httpRequest.getRequestURI();
            boolean isOpenapiPath = openApiConfig.getExcludeOpenapiPaths().stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
            if(isOpenapiPath) {
                // 设置请求属性，标记为可跳过验证
                servletRequest.setAttribute(BYPASS_ATTRIBUTE, true);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
