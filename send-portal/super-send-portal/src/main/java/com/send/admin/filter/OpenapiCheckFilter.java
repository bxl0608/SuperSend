package com.send.admin.filter;

import com.send.admin.config.OpenApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
            if (isOpenapiPath) {
                // 设置请求属性，标记为可跳过验证
                servletRequest.setAttribute(BYPASS_ATTRIBUTE, true);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
