package com.send.admin.service.biz.config;

import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.admin.service.biz.config.param_config.ParamConfigService;
import com.send.admin.service.biz.config.picture_config.PictureConfigService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.send.model.db.mysql.bo.config.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class ConfigService {
    @Autowired
    private ParamConfigService paramConfigService;
    @Autowired
    private PictureConfigService pictureConfigService;

    public int save(ConfigRequestBo bo) {
        return paramConfigService.save(bo);
    }

    public TbConfigBo<IConfigDetail> findByType(String type) {
        return paramConfigService.findByType(type);
    }

    public TbConfigBo<LoginConfigDetail> findLoginConfig() {
        return paramConfigService.findByDetailClass(LoginConfigDetail.class);
    }

    public TbConfigBo<PasswordConfigDetail> findPasswordConfig() {
        return paramConfigService.findByDetailClass(PasswordConfigDetail.class);
    }

    public TbConfigBo<CreditConfigDetail> findCreditConfig() {
        return paramConfigService.findByDetailClass(CreditConfigDetail.class);
    }

    public TbConfigBo<PersonalizedConfigDetail> findPersonalizedConfig() {
        return paramConfigService.findByDetailClass(PersonalizedConfigDetail.class);
    }

    public TbConfigBo<SystemTimeConfigDetail> findSystemTimeConfig() {
        return paramConfigService.findByDetailClass(SystemTimeConfigDetail.class);
    }

    public TbConfigBo<SystemTimeConfigDetail> ntp(String ntpHostname) {
        paramConfigService.ntp(ntpHostname);
        return findSystemTimeConfig();
    }

    public void uploadPictures(MultipartHttpServletRequest request) {
        pictureConfigService.uploadPictures(request);
    }

    public void downloadPicture(String type, HttpServletResponse response) {
        pictureConfigService.downloadPicture(type, response);
    }
}
