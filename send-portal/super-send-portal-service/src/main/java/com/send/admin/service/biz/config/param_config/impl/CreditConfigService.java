package com.send.admin.service.biz.config.param_config.impl;

import com.send.common.tool.IpV4Tool;
import com.send.model.contant.ConfigTypeConstant;
import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.impl.CreditConfigDetail;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.config.param_config.AbstractConfigDetailService;
import com.send.admin.service.bo.config.ConfigRequestBo;
import com.project.base.common.json.JsonTool;
import com.project.base.model.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class CreditConfigService extends AbstractConfigDetailService {
    @Override
    public String getType() {
        return ConfigTypeConstant.CREDIT_CONFIG;
    }

    @Override
    public Class<? extends IConfigDetail> getConfigDetailClass() {
        return CreditConfigDetail.class;
    }

    @Override
    public int save(ConfigRequestBo bo) {
        CreditConfigDetail detail = JsonTool.toObject(JsonTool.toJson(bo.getDetail()), CreditConfigDetail.class);
        if (detail == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_DETAIL);
        }
        if (detail.getCreditSwitch() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CONFIG_CREDIT_SWITCH);
        }
        if (CollectionUtils.isNotEmpty(detail.getCreditIps())) {
            List<String> ips = detail.getCreditIps().stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            boolean anyMatched = ips.stream().anyMatch(this::validateIp);
            if (!anyMatched) {
                throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CONFIG_IP);
            }
            // 记录去重后的IP列表
            detail.setCreditIps(ips);
        }

        // 保存 或 更新
        return buildAndSave(bo, detail);
    }

    private boolean validateIp(String inputIp) {
        if (inputIp.contains("-")) {
            String[] result = inputIp.split("-");
            if (result.length != 2) {
                return false;
            }
            for (String ip : result) {
                if (!IpV4Tool.isIpV4(ip)) {
                    return false;
                }
            }
            return true;
        } else if (inputIp.contains("/")) {
            return IpV4Tool.isIpV4Subnet(inputIp);
        } else {
            return IpV4Tool.isIpV4(inputIp);
        }
    }
}
