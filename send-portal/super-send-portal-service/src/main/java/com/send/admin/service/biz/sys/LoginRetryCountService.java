package com.send.admin.service.biz.sys;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.db.mysql.bo.config.impl.LoginConfigDetail;
import com.send.admin.service.biz.config.ConfigEvent;
import com.send.admin.service.biz.config.ParamConfigCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class LoginRetryCountService {
    @Autowired
    private RetryCountCacheConfig retryCountCacheConfig;

    public int get(String username) {
        AtomicInteger atomicInteger = retryCountCacheConfig.get(username);
        return atomicInteger == null ? 0 : atomicInteger.get();
    }

    public int incrementAndGet(String username) {
        AtomicInteger atomicInteger = retryCountCacheConfig.get(username);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(0);
            retryCountCacheConfig.put(username, atomicInteger);
        }
        // 防止递增变成负值
        if (atomicInteger.get() == Integer.MAX_VALUE || atomicInteger.get() < 0) {
            atomicInteger.set(Integer.MAX_VALUE);
            return Integer.MAX_VALUE;
        }
        return atomicInteger.incrementAndGet();
    }

    public void clear(String username) {
        retryCountCacheConfig.delete(username);
    }

    public Set<String> getAllUsername() {
        return retryCountCacheConfig.retryCountCache.asMap().keySet();
    }

    @Component
    public static class RetryCountCacheConfig implements InitializingBean {

        @Autowired
        private ParamConfigCacheService paramConfigCacheService;

        /**
         * 最大缓存数量：默认200
         */
        @Value("${loginRetryCount.cache.maximumSize:200}")
        private long maximumSize;

        /**
         * 登录次数统计缓存
         *
         * @return 缓存管理器
         */
        private Cache<String, AtomicInteger> retryCountCache;

        @Override
        public void afterPropertiesSet() {
            retryCountCache = buildCache();
        }

        private Cache<String, AtomicInteger> buildCache() {
            return Caffeine.newBuilder()
                    // 设置最后一次写入或访问后经过固定时间过期
                    .expireAfterAccess(paramConfigCacheService.getLoginConfigDetail().getLockDuration(), TimeUnit.MINUTES)
                    // 初始的缓存空间大小
                    .initialCapacity(10)
                    // 缓存的最大条数
                    .maximumSize(maximumSize)
                    .build();
        }

        public void put(String key, AtomicInteger value) {
            if (key == null) {
                return;
            }
            retryCountCache.put(key, value);
        }

        public AtomicInteger get(String key) {
            if (key == null) {
                return null;
            }
            return retryCountCache.getIfPresent(key);
        }

        public void delete(String key) {
            if (key == null) {
                return;
            }
            retryCountCache.invalidate(key);
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

            int oldDuration = ObjectUtils.defaultIfNull(findDuration(configEvent.getOldBo()), ParamConfigCacheService.DEFAULT_LOCK_DURATION);
            int newDuration = ObjectUtils.defaultIfNull(findDuration(configEvent.getNewBo()), ParamConfigCacheService.DEFAULT_LOCK_DURATION);
            if (oldDuration == newDuration) {
                return;
            }

            Cache<String, AtomicInteger> old = retryCountCache;
            Cache<String, AtomicInteger> cache = buildCache();
            cache.putAll(old.asMap());
            retryCountCache = cache;
            old.invalidateAll();
        }

        private Integer findDuration(TbConfigBo<IConfigDetail> oldBo) {
            if (oldBo != null && oldBo.getDetail() != null) {
                IConfigDetail detail = oldBo.getDetail();
                if (detail instanceof LoginConfigDetail) {
                    LoginConfigDetail loginConfigDetail = (LoginConfigDetail) detail;
                    return loginConfigDetail.getLockDuration();
                }
            }
            return null;
        }
    }
}
