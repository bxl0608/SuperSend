package com.send.admin.service.biz.sys.auth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.send.common.security.PasswordTool;
import com.send.dao.repository.TbSysUserDao;
import com.send.model.db.mysql.TbSysUser;
import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.sys.auth.event.AuthEvent;
import com.send.admin.service.biz.sys.auth.event.AuthEventPublisher;
import com.send.admin.service.bo.sys.LoginRequestBO;
import com.send.admin.service.tool.RsaLocalTool;
import com.project.base.model.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Component
public class UsernamePasswordProcessor {

    @Resource
    private TbSysUserDao tbSysUserDao;

    @Autowired
    private AuthEventPublisher authEventPublisher;

    public void process(LoginRequestBO loginBO) throws IOException, NoSuchAlgorithmException {

        /* 查询用户 */
        TbSysUser tbSysUser = tbSysUserDao.selectOne(Wrappers.lambdaQuery(TbSysUser.class)
                .eq(TbSysUser::getUsername, loginBO.getUsername()));

        if (tbSysUser == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_USER_OR_PASSWORD);
        }

        /* 解密密码 */
        String passwordHash;
        try {
            DecryptPassword decryptPassword = decrypt(loginBO);
            String passwordSalt = tbSysUser.getPasswordSalt();
            passwordHash = PasswordTool.hash(passwordSalt, decryptPassword.getPassword());
        } catch (Exception e) {
            log.error("解密密码失败:" + e.getMessage(), e);
            publishFailedEvent(loginBO);
            throw new BusinessException(MasterExceptionEnum.ERR_USER_OR_PASSWORD);
        }

        /* 验证密码 */
        if (!tbSysUser.getPassword().equals(passwordHash)) {
            publishFailedEvent(loginBO);
            throw new BusinessException(MasterExceptionEnum.ERR_USER_OR_PASSWORD);
        }

    }

    /**
     * 验证失败，发出事件
     * 1、记录重试次数
     * 2、记录审计日志
     * 等..
     *
     * @param loginRequestBO 入参
     * @see RetryCountProcessor 处理
     */
    private void publishFailedEvent(LoginRequestBO loginRequestBO) {

        AuthEvent authEvent = new AuthEvent(this);
        authEvent.setAuthResult(AuthEvent.AuthResultEnum.LOGIN_FAIL);
        authEvent.setUsername(loginRequestBO.getUsername());
        authEventPublisher.publishAuthEvent(authEvent);
    }

    /**
     * 解密密码
     *
     * @param loginBO 登录入参
     * @return 密码解密后信息
     */
    public DecryptPassword decrypt(LoginRequestBO loginBO) {

        String password1 = loginBO.getPassword();
        /* 解密(密码+验证码id) */
        String plainPassword = RsaLocalTool.privateDecrypt(password1);
        DecryptPassword decryptPassword = new DecryptPassword();
        decryptPassword.setPassword(plainPassword);
        return decryptPassword;
    }


    @Data
    public static class DecryptPassword {
        private String password;
        private String verifyCodeId;
    }
}
