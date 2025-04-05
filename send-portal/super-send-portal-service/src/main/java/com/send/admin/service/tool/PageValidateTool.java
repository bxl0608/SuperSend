package com.send.admin.service.tool;

import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.project.base.model.exception.BusinessException;
import com.project.base.model.pagination.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public class PageValidateTool {
    public static final String ORDER_FIELD_REGEX = "^[A-Za-z0-9][A-Za-z0-9@\\-_.:]{1,29}$";

    private PageValidateTool() {
    }

    public static void pageValidate(PageRequest page) {
        if (page == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_PAGE);
        }
        if (page.getCurrentPage() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_CURRENT_PAGE);
        }
        if (page.getPageSize() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_PAGE_SIZE);
        }
        if (page.getCurrentPage() <= 0) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_CURRENT_PAGE);
        }
        if (page.getPageSize() <= 0) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_PAGE_SIZE);
        }
        if (CollectionUtils.isNotEmpty(page.getOrderInfoList())) {
            for (PageRequest.OrderInfo orderInfo : page.getOrderInfoList()) {
                if (StringUtils.isNotBlank(orderInfo.getDirection())
                        && !StringUtils.equalsAnyIgnoreCase(orderInfo.getDirection(), PageRequest.OrderInfo.ASC, PageRequest.OrderInfo.DESC)) {
                    throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_PAGE_ORDER_DIRECTION);
                }
                if (StringUtils.isNotBlank(orderInfo.getField()) && !orderInfo.getField().matches(ORDER_FIELD_REGEX)) {
                    throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_PAGE_ORDER_FIELD);
                }
            }
        }
    }
}
