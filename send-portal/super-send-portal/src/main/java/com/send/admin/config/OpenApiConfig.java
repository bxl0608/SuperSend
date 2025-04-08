package com.send.admin.config;

import com.send.admin.service.biz.sys.TokenService;
import com.send.model.auth.UserDetail;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月08日 15:27
 * @description：
 */
@Component
@Data
public class OpenApiConfig {
    @Value("#{'${auth.openapi.paths:/portal/openapi/test,/protal/openapi/*}'.split('\\s*,\\s*')}")
    private Set<String> excludeOpenapiPaths;

    @Value("${auth.openapi.token:568d982048444d80985016125c97777d}")
    private String openapiToken;

    @Autowired
    private TokenService.TokenCache tokenCache;

    @PostConstruct
    public void initOpenapi(){
        initOpenapiToken();
    }

    public String initOpenapiToken() {
        String token = openapiToken;
        UserDetail userDetail = new UserDetail();
        userDetail.setPermissionURIList(excludeOpenapiPaths.stream().collect(Collectors.toCollection(ArrayList::new)));
        TokenService.TokenRefData tokenRefData = new TokenService.TokenRefData();
        tokenRefData.setUserDetail(userDetail);
        tokenCache.put(token, tokenRefData);
        return token;
    }
}
