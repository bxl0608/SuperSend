package com.send.admin.service.biz.sys.auth.event;

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
public class AuthEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishAuthEvent(AuthEvent authEvent) {
        applicationEventPublisher.publishEvent(authEvent);
    }
}
