package com.send.admin.filter;

import com.send.common.tool.IpV4Tool;
import com.send.common.tool.IpV6Tool;
import com.send.model.db.mysql.bo.config.impl.CreditConfigDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.config.ParamConfigCacheService;
import com.project.base.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
@Order(1)
public class AuthIpCheckFilter implements Filter {
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR", "X-Real-IP"};

    @Autowired
    private ParamConfigCacheService paramConfigCacheService;

    @Value("#{'${auth.creditExclude:}'.split(',')}")
    private List<String> excludeList;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        log.debug("*** filter 1 : start execute ...");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);

        CreditConfigDetail creditConfigDetail = paramConfigCacheService.getCreditConfigDetail();
        if (creditConfigDetail == null || BooleanUtils.isNotTrue(creditConfigDetail.getCreditSwitch()) ||
                CollectionUtils.isEmpty(creditConfigDetail.getCreditIps())) {
            filterChain.doFilter(request, wrapper);
            return;
        }
        if (CollectionUtils.isNotEmpty(excludeList)) {
            boolean matched = excludeList.stream().anyMatch(exclude -> antPathMatcher.match(exclude, request.getRequestURI()));
            if (matched) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 客户端的IP
        String clientIp = getClientIpAddress(request);

        // 判断客户端IP是否被允许访问
        if (isInCreditIp(clientIp, creditConfigDetail.getCreditIps())) {
            filterChain.doFilter(request, wrapper);
            return;
        }

        log.error("clientIp={}", clientIp);

        throw new BusinessException(MasterExceptionEnum.ERR_CREDIT_IP);
    }

    private boolean isInCreditIp(String ip, List<String> creditIps) {
        if (IpV4Tool.isIpV4(ip)) {
            for (String creditIp : creditIps) {
                if (creditIp.contains("/")) {
                    // cidr
                    if (IpV4Tool.ipInSubnet(ip, creditIp)) {
                        return true;
                    }
                } else if (creditIp.contains("-")) {
                    // IP1-IP2
                    if (IpV4Tool.ipInRange(ip, creditIp)) {
                        return true;
                    }
                } else if (creditIp.equals(ip)) {
                    // 单个IP
                    return true;
                }
            }
        } else if (IpV6Tool.isIpV6(ip)) {
            return "0:0:0:0:0:0:0:1".equals(ip);
        }

        return false;
    }

    /***
     * 获取客户端ip地址
     * @param request 请求对象
     * @return 客户端ip地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // get first ip from proxy ip
                int index = ip.indexOf(',');
                if (index != -1) {
                    return ip.substring(0, index);
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
