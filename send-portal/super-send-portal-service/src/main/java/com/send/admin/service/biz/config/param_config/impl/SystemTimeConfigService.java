package com.send.admin.service.biz.config.param_config.impl;

import com.send.common.BaseInvoke;
import com.send.common.tool.IpV4Tool;
import com.send.common.tool.IpV6Tool;
import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import com.send.model.db.mysql.bo.config.impl.SystemTimeConfigDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.param_config.AbstractConfigDetailService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.send.admin.service.biz.constants.FilePathConstant;
import com.project.base.common.json.JsonTool;
import com.project.base.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class SystemTimeConfigService extends AbstractConfigDetailService {

    private static final String NTPDATE_COMMAND = "ntpdate -u ";
    private static final String NTP_COMMAND = "sh " + FilePathConstant.SYSTEM_NTP + " ";

    @Override
    public String getType() {
        return ConfigTypeConstant.SYSTEM_TIME_CONFIG;
    }

    @Override
    public Class<? extends IConfigDetail> getConfigDetailClass() {
        return SystemTimeConfigDetail.class;
    }

    @Override
    public int save(ConfigRequestBo bo) {
        SystemTimeConfigDetail detail = JsonTool.toObject(JsonTool.toJson(bo.getDetail()), SystemTimeConfigDetail.class);
        if (detail == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_DETAIL);
        }
        //
        if (detail.getNetworkEnable() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_NETWORK_ENABLE);
        }
        if (BooleanUtils.isTrue(detail.getNetworkEnable())) {
            String ntpServerAddress = detail.getNtpServerAddress();
            if (StringUtils.isBlank(ntpServerAddress)) {
                throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_CONFIG_NTP_SERVER_ADDRESS);
            }
            if (!validateNtpServerAddress(ntpServerAddress)) {
                throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_NTP_SERVER_ADDRESS);
            }
        }

        // 保存 或 更新
        return buildAndSave(bo, detail);
    }

    public boolean validateNtpServerAddress(String ntpServerAddress) {
        return IpV4Tool.isIpV4(ntpServerAddress) || validateDomain(ntpServerAddress) || IpV6Tool.isIpV6(ntpServerAddress);
    }

    @Override
    public TbConfigBo<IConfigDetail> findByType(String type) {
        TbConfigBo<IConfigDetail> tbConfigBo = super.findByType(type);
        buildOtherInfo(tbConfigBo.getDetail());
        return tbConfigBo;
    }

    @Override
    public <T extends IConfigDetail> TbConfigBo<T> findByDetailClass(Class<T> clazz) {
        TbConfigBo<T> byDetailClass = super.findByDetailClass(clazz);
        buildOtherInfo(byDetailClass.getDetail());
        return byDetailClass;
    }

    private void buildOtherInfo(IConfigDetail iConfigDetail) {
        if (iConfigDetail instanceof SystemTimeConfigDetail) {
            SystemTimeConfigDetail detail = (SystemTimeConfigDetail) iConfigDetail;
            detail.setSystemTime(LocalDateTime.now());
            detail.setTimeZone(TimeZone.getDefault());
        }
    }

    /**
     * ntp同步服务器时间
     *
     * @param hostName ntp主机
     */
    public void ntp(String hostName) {
        if (StringUtils.isBlank(hostName)) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_NTP_HOST_NAME);
        }
        if (!validateNtpServerAddress(hostName)) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_NTP_HOST_NAME);
        }
        //执行同步
        String result = BaseInvoke.invokeScript(NTPDATE_COMMAND + hostName);
        log.info("[ParamConfigService] ntp result:{}", result);
        if (StringUtils.isBlank(result) || result.contains("no server suitable")) {
            throw new BusinessException(MasterExceptionEnum.ERR_NOT_AVAILABLE, I18nParamConstant.PARAM_NTP_HOST_NAME);
        }
        BaseInvoke.invokeScript(NTP_COMMAND + hostName);
    }

    /**
     * 关闭外部ntp服务器同步
     */
    public void ntpStop() {
        // 执行同步
        BaseInvoke.invokeScript(NTP_COMMAND);
    }

    private static final String DOMAIN_REGEX = "^(http://www\\.|https://www\\.|http://|https://)?[a-zA-Z0-9]+([\\-.][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,5}$";

    public static boolean validateDomain(String domain) {
        return domain.matches(DOMAIN_REGEX);
    }
}
