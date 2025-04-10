package com.send.admin.service.biz.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.project.base.common.json.JsonTool;
import com.send.dao.repository.TbConfigDao;
import com.send.model.db.mysql.TbConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * @author zhanghc
 * @description:
 * @date 2022-10-20 14:14
 **/
@Slf4j
@Component
public class I18nService implements InitializingBean {

    public static final String LOCALE_EN_US = "en-US";
    public static final String LOCALE_EN = "en";
    public static final String LOCALE_ZH_CN = "zh-CN";

    private Locale currentLocale = Locale.SIMPLIFIED_CHINESE;

    @Autowired
    private TbConfigDao tbConfigDao;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void afterPropertiesSet() throws Exception {

        /* 从数据库中获取安装选择的语言 */
        List<TbConfig> langConfigList = tbConfigDao.selectList(Wrappers.lambdaQuery(TbConfig.class)
                .eq(TbConfig::getType, "langConfig"));
        if (CollectionUtils.isEmpty(langConfigList)) {
            return;
        }

        try {
            TbConfig paramConfig = langConfigList.get(0);
            String lang = JsonTool.readTree(paramConfig.getDetail()).get("lang").asText();
            if (LOCALE_EN_US.equals(lang)) {
                currentLocale = new Locale("en", "US");
            } else if (LOCALE_ZH_CN.equals(lang)) {
                currentLocale = new Locale("zh", "CN");
            } else {
                log.error("Incorrect system language configuration , please check! ");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 获取当前locale
     *
     * @return 语言对象
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * 根据页面传入语言，获取消息
     *
     * @param messageKey 模板
     * @return 国际化结果
     */
    public String getMessage(String messageKey) {
        return getMessage(messageKey, null);
    }

    /**
     * 根据系统基础语言，获取消息
     *
     * @param messageKey 模板
     * @return 国际化结果
     */
    public String getBasicMessage(String messageKey) {
        if (StringUtils.isBlank(messageKey)) {
            return null;
        }
        return messageSource.getMessage(messageKey, null, messageKey, currentLocale);
    }

    public String getBasicMessage(String messageKey, Object[] args) {
        return messageSource.getMessage(messageKey, args, messageKey, currentLocale);
    }

    /**
     * 根据当前安装的语言，获取消息
     *
     * @param messageKey 模板
     * @param args       参数列表
     * @return 国际化结果
     */
    public String getMessage(String messageKey, Object[] args) {
        return getMessage(messageKey, args, messageKey);
    }

    /**
     * 根据当前安装的语言，获取消息
     *
     * @param messageKey     模板
     * @param args           参数列表
     * @param defaultMessage 默认值
     * @return 国际化结果
     */
    public String getMessage(String messageKey, Object[] args, String defaultMessage) {
        if (StringUtils.isBlank(messageKey)) {
            return null;
        }
        return messageSource.getMessage(messageKey.trim(), args, defaultMessage, LocaleContextHolder.getLocale());
    }

    public Locale getLocale(String lang) {
        if (LOCALE_EN_US.equals(lang) || LOCALE_EN.equals(lang)) {
            return Locale.US;
        } else {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }
}
