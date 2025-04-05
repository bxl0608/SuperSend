package com.send.admin.controller.sys;

import com.send.admin.service.bo.sys.LoginRequestBO;
import com.send.admin.service.bo.sys.LoginResponseBO;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.tool.RsaLocalTool;
import com.send.admin.vo.sys.LoginRequestVO;
import com.project.base.model.web.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@RestController
@RequestMapping("/auth")
@Api(tags = "登录模块")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public CommonResponse<LoginResponseBO> login(@Valid @RequestBody LoginRequestVO loginRequestVO) throws IOException, NoSuchAlgorithmException {

        LoginRequestBO logonBO = new LoginRequestBO();
        logonBO.setPassword(loginRequestVO.getPassword());
        logonBO.setUsername(loginRequestVO.getUsername());
        logonBO.setVerifyCode(loginRequestVO.getVerifyCode());
        logonBO.setVerifyCodeId(loginRequestVO.getVerifyCodeId());

        LoginResponseBO loginResponseBO = authService.login(logonBO);

        return CommonResponse.builder(loginResponseBO).build();
    }

    @GetMapping("/publicRsaKey")
    @ApiOperation(value = "公钥")
    public CommonResponse<String> findPublicRsaKey() {
        String publicKey = RsaLocalTool.getPublicKey();
        return CommonResponse.builder(publicKey).build();
    }

    @GetMapping("/publicRsaKeyEncrypt")
    @ApiOperation(value = "公钥加密")
    public CommonResponse<String> publicRsaKeyEncrypt(@RequestParam(value = "plain") String plain) {
        String encryptTxt = RsaLocalTool.publicEncrypt(plain);
        return CommonResponse.builder(encryptTxt).build();
    }


    @GetMapping("/logout")
    @ApiOperation(value = "登出")
    public CommonResponse<String> logout() {
        authService.logout();
        return CommonResponse.builder("success").build();
    }
}
