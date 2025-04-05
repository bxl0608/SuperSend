package com.send.admin.service.biz.sys;

import com.send.model.auth.UserDetail;
import com.send.model.enums.UserExpireType;
import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.sys.auth.RetryCountProcessor;
import com.send.admin.service.biz.sys.auth.UserDetailProcessor;
import com.send.admin.service.biz.sys.auth.UsernamePasswordProcessor;
import com.send.admin.service.biz.sys.auth.event.AuthEvent;
import com.send.admin.service.biz.sys.auth.event.AuthEventPublisher;
import com.send.admin.service.bo.sys.LoginRequestBO;
import com.send.admin.service.bo.sys.LoginResponseBO;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
@Slf4j
public class AuthService {

    public static final String KEY_TOKEN = "TOKEN";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_USER_DETAIL = "USER_DETAIL";

    @Autowired
    private UsernamePasswordProcessor usernamePasswordProcessor;

    @Autowired
    private RetryCountProcessor retryCountProcessor;

    @Autowired
    private UserDetailProcessor userDetailProcessor;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthEventPublisher authEventPublisher;

    /**
     * 登录
     *
     * @param loginRequestBO 入参
     * @return 结果
     * @throws IOException              io异常
     * @throws NoSuchAlgorithmException 算法异常
     */
    public LoginResponseBO login(LoginRequestBO loginRequestBO) throws IOException, NoSuchAlgorithmException {

        /* 处理重试次数 */
        retryCountProcessor.process(loginRequestBO);


        /* 处理用户名密码是否匹配 */
        usernamePasswordProcessor.process(loginRequestBO);

        /* 处理用户详细信息 */
        UserDetail userDetail = userDetailProcessor.process(loginRequestBO);

        // 判断用户有效期是否过期
        if (UserExpireType.TEMPORARY.getType().equals(userDetail.getExpireType()) &&
                userDetail.getExpireDate() != null && userDetail.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(MasterExceptionEnum.ERR_USER_EXPIRE);
        }

        /* 生成token */
        String token = tokenService.genToken(userDetail);

        /* 发布登录成功事件 */
        AuthEvent authEvent = new AuthEvent(this);
        authEvent.setAuthResult(AuthEvent.AuthResultEnum.LOGIN_SUCCESS);
        authEvent.setUserDetail(userDetail);
        authEvent.setToken(token);
        authEvent.setUsername(userDetail.getUsername());
        authEventPublisher.publishAuthEvent(authEvent);

        /* 构建登录返回结果 */
        LoginResponseBO loginResponseBO = buildResponse(token, userDetail);

        return loginResponseBO;
    }


    /**
     * @param token      口令
     * @param userDetail 用户详情
     * @return 登录结果
     */
    private LoginResponseBO buildResponse(String token, UserDetail userDetail) {
        LoginResponseBO loginResponseBO = new LoginResponseBO();
        loginResponseBO.setBuiltinFlag(userDetail.getBuiltinFlag());
        loginResponseBO.setNickName(userDetail.getNickName());
        loginResponseBO.setRoleList(userDetail.getRoleList());
        loginResponseBO.setUsername(userDetail.getUsername());
        loginResponseBO.setUserId(userDetail.getId());
        loginResponseBO.setToken(token);
        return loginResponseBO;
    }

    /**
     * 登出
     * 1：删除缓存的token
     */
    public void logout() {
        String token = ThreadContext.get(KEY_TOKEN);
        tokenService.deleteToken(token);
    }
}
