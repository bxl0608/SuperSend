package com.send.admin.service.biz.config.event;

import com.send.admin.service.biz.config.ConfigEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Component
public class ConfigEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishConfigEvent(ConfigEvent authEvent) {
        applicationEventPublisher.publishEvent(authEvent);
    }
}
