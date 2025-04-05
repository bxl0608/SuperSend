package com.send.admin.service.biz.sys.auth.event;

import com.send.model.auth.UserDetail;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AuthEvent extends ApplicationEvent {
    private static final long serialVersionUID = -5364533358425247122L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AuthEvent(Object source) {
        super(source);
    }

    /**
     * 登录结果
     *
     * @see AuthResultEnum
     */
    private AuthResultEnum authResult;

    /**
     * 登录成功后的token
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户详细信息
     */
    private UserDetail userDetail;


    public enum AuthResultEnum {
        LOGIN_SUCCESS, LOGIN_FAIL, TOKEN_SUCCESS
    }

}
