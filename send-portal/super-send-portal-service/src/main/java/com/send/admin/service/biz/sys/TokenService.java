package com.send.admin.service.biz.sys;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.send.model.auth.UserDetail;
import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.db.mysql.bo.config.impl.LoginConfigDetail;
import com.send.admin.service.biz.config.ConfigEvent;
import com.send.admin.service.biz.config.ParamConfigCacheService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class TokenService {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private TokenCache tokenCache;

    /**
     * 生成token
     *
     * @return token
     */
    public String genToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 1、生成token
     * 2、以token 进行缓存 userDetail
     *
     * @return token
     */
    public String genToken(UserDetail userDetail) {
        String token = genToken();

        TokenRefData tokenRefData = new TokenRefData();
        tokenRefData.setSsoToken(genToken());
        tokenRefData.setUserDetail(userDetail);
        tokenCache.put(token, tokenRefData);
        return token;
    }

    /**
     * 获取 token
     *
     * @param token token
     * @return UserDetail
     */
    public UserDetail getUserDetail(String token) {
        TokenRefData tokenRefData = tokenCache.get(token);
        if (tokenRefData == null) {
            return null;
        }
        return tokenRefData.getUserDetail();
    }

    /**
     * 获取sso token
     *
     * @param token token
     * @return SsoToken
     */
    public String getSsoToken(String token) {
        TokenRefData tokenRefData = tokenCache.get(token);
        if (tokenRefData == null) {
            return null;
        }
        return tokenRefData.getSsoToken();
    }

    /**
     * 清除用户的缓存
     *
     * @param userId 用户id
     */
    public void clearByUserId(Integer userId) {
        tokenCache.clearByUserId(userId);
    }

    /**
     * 缓存的用户信息
     *
     * @param userDetail userDetail
     */
    public void updateUserDetail(UserDetail userDetail) {
        tokenCache.updateUserDetail(userDetail);
    }

    /**
     * 除了指定的token，主动移除用户的其他token
     *
     * @param userId        用户id
     * @param reservedToken 需要保留的token
     */
    public void deleteTokenByUserDetailButTheGivenOne(Integer userId, String reservedToken) {
        tokenCache.deleteTokenByUserDetailButTheGivenOne(userId, reservedToken);
    }

    /**
     * 删除 token
     *
     * @param token token
     */
    public void deleteToken(String token) {
        tokenCache.delete(token);
    }

    /**
     * 检查ssoToken是否合法
     *
     * @param ssoToken ssoToken
     * @return boolean
     */
    public boolean checkSsoToken(String ssoToken) {
        for (TokenRefData item : tokenCache.cache.asMap().values()) {
            if (item.getSsoToken().equals(ssoToken)) {
                return true;
            }
        }
        return false;
    }


    @Component
    public static class TokenCache implements InitializingBean {
        /**
         * 默认缓存时间：120分钟
         */
        public static final int DEFAULT_DURATION = 120;

        /**
         * 最大缓存数量：默认200
         */
        @Value("${token.cache.maximumSize:200}")
        private long maximumSize;

        @Autowired
        private ParamConfigCacheService paramConfigCacheService;

        /**
         * 验证码缓存
         *
         * @return 缓存管理器
         */
        private Cache<String, TokenRefData> cache;

        @Override
        public void afterPropertiesSet() {
            cache = buildCache();
        }

        private Cache<String, TokenRefData> buildCache() {
            return Caffeine.newBuilder()
                    // 设置最后一次写入或访问后经过固定时间过期
                    .expireAfterAccess(findSessionDurationFromDb(), TimeUnit.MINUTES)
                    // 初始的缓存空间大小
                    .initialCapacity(10)
                    // 缓存的最大条数
                    .maximumSize(maximumSize)
                    .build();
        }

        public void put(String key, TokenRefData value) {
            if (key == null) {
                return;
            }
            cache.put(key, value);
        }

        public TokenRefData get(String key) {
            if (key == null) {
                return null;
            }

            return cache.getIfPresent(key);
        }

        public void delete(String key) {
            if (key == null) {
                return;
            }
            cache.invalidate(key);
        }

        /**
         * 清除用户关联的缓存
         *
         * @param userId 用户id
         */
        public void clearByUserId(Integer userId) {
            if (userId == null) {
                return;
            }
            String lock = buildLockByUserId(userId);
            synchronized (lock.intern()) {
                List<String> keyList = cache.asMap().entrySet().stream().filter(entry -> {
                    TokenRefData tokenRefData = entry.getValue();
                    if (tokenRefData.getUserDetail() == null || tokenRefData.getUserDetail().getId() == null) {
                        return false;
                    }
                    return tokenRefData.getUserDetail().getId().equals(userId);
                }).map(Map.Entry::getKey).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(keyList)) {
                    cache.invalidateAll(keyList);
                }
            }
        }

        public void updateUserDetail(UserDetail inputUserDetail) {
            if (inputUserDetail == null || inputUserDetail.getId() == null) {
                return;
            }
            UserDetail userDetail = new UserDetail();
            BeanUtils.copyProperties(inputUserDetail, userDetail);
            // 加锁
            String lock = buildLockByUserId(userDetail.getId());
            synchronized (lock.intern()) {
                cache.asMap().values().stream()
                        .filter(tokenRefData -> tokenRefData.getUserDetail() != null && userDetail.getId().equals(tokenRefData.getUserDetail().getId()))
                        .forEach(tokenRefData -> tokenRefData.setUserDetail(userDetail));
            }
        }

        private String buildLockByUserId(Integer userId) {
            return "userDetailCacheByUserId:" + userId;
        }


        private long findSessionDurationFromDb() {
            Integer sessionDuration = paramConfigCacheService.getLoginConfigDetail().getSessionDuration();
            if (sessionDuration == 0) {
                sessionDuration = 10000;
            }
            return sessionDuration;
        }

        /**
         * 当锁定时间发生变化时，重建缓存
         */
        @EventListener(value = ConfigEvent.class)
        public void rebuildCache(ConfigEvent configEvent) {
            if (configEvent == null || configEvent.getNewBo() == null) {
                return;
            }
            if (!ConfigTypeConstant.LOGIN_CONFIG.equals(configEvent.getNewBo().getType())) {
                return;
            }

            int oldDuration = ObjectUtils.defaultIfNull(findDuration(configEvent.getOldBo()), DEFAULT_DURATION);
            int newDuration = ObjectUtils.defaultIfNull(findDuration(configEvent.getNewBo()), DEFAULT_DURATION);
            if (oldDuration == newDuration) {
                return;
            }

            Cache<String, TokenRefData> old = cache;
            Cache<String, TokenRefData> newCache = buildCache();
            newCache.putAll(old.asMap());
            this.cache = newCache;
            old.invalidateAll();
        }

        private Integer findDuration(TbConfigBo<IConfigDetail> oldBo) {
            if (oldBo != null && oldBo.getDetail() != null) {
                IConfigDetail detail = oldBo.getDetail();
                if (detail instanceof LoginConfigDetail) {
                    LoginConfigDetail loginConfigDetail = (LoginConfigDetail) detail;
                    return loginConfigDetail.getSessionDuration();
                }
            }
            return null;
        }

        /**
         * 除了指定的token，主动移除用户的其他token
         *
         * @param userId        用户id
         * @param reservedToken 需要保留的token
         */
        public void deleteTokenByUserDetailButTheGivenOne(Integer userId, String reservedToken) {
            if (userId == null) {
                return;
            }
            // 加锁
            String lock = buildLockByUserId(userId);
            synchronized (lock.intern()) {
                List<String> tokenList = cache.asMap().entrySet().stream().filter(entry -> {
                    if (StringUtils.equals(reservedToken, entry.getKey())) {
                        return false;
                    }
                    TokenRefData tokenRefData = entry.getValue();
                    UserDetail userDetail = tokenRefData.getUserDetail();
                    return userDetail != null && userId.equals(userDetail.getId());
                }).map(Map.Entry::getKey).distinct().collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(tokenList)) {
                    cache.invalidateAll(tokenList);
                }
            }
        }
    }

    @Data
    public static class TokenRefData {
        private UserDetail userDetail;
        private String ssoToken;
    }

    /**
     * 从请求中获取认证口令Authorization的值
     *
     * @param servletRequest 入参
     * @return 出参
     */
    public static String getAuthorizationFromRequest(ServletRequest servletRequest) {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String accessToken = request.getHeader(AUTHORIZATION_HEADER);
            if (StringUtils.isBlank(accessToken)) {
                accessToken = request.getParameter(AUTHORIZATION_HEADER);
            }
            return accessToken;
        }
        return servletRequest.getParameter(AUTHORIZATION_HEADER);
    }

}
