package com.send.admin.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.project.base.common.json.JsonTool;
import com.project.base.i18n.I18nMessage;
import com.send.dao.repository.TbConfigDao;
import com.send.model.db.mysql.TbConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Configuration
@Slf4j
public class WebConfig {

    public static final String LOCALE_EN_US = "en-US";
    public static final String LOCALE_EN = "en";
    public static final String LOCALE_ZH_CN = "zh-CN";
    @Autowired
    private TbConfigDao tbConfigDao;

    @Bean
    public I18nMessage i18nMessage() {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        /* 从数据库中获取安装选择的语言 */
        List<TbConfig> langConfigList = tbConfigDao.selectList(Wrappers.lambdaQuery(TbConfig.class)
                .eq(TbConfig::getType, "langConfig"));
        if (CollectionUtils.isNotEmpty(langConfigList)) {
            try {
                TbConfig paramConfig = langConfigList.get(0);
                String lang = JsonTool.readTree(paramConfig.getDetail()).get("lang").asText();
                if (LOCALE_EN_US.equals(lang)) {
                    locale = new Locale("en", "US");
                } else if (LOCALE_ZH_CN.equals(lang)) {
                    locale = new Locale("zh", "CN");
                } else {
                    log.error("Incorrect system language configuration , please check! ");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return new I18nMessage(locale);
    }

/*    @Bean
    public SessionLocaleResolver localeResolver() {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        *//* 从数据库中获取安装选择的语言 *//*
        List<TbConfig> langConfigList = tbConfigDao.selectList(Wrappers.lambdaQuery(TbConfig.class)
                .eq(TbConfig::getType, "langConfig"));
        if (CollectionUtils.isNotEmpty(langConfigList)) {
            try {
                TbConfig paramConfig = langConfigList.get(0);
                String lang = JsonTool.readTree(paramConfig.getDetail()).get("lang").asText();
                if (LOCALE_EN_US.equals(lang)) {
                    locale = new Locale("en", "US");
                } else if (LOCALE_ZH_CN.equals(lang)) {
                    locale = new Locale("zh", "CN");
                } else {
                    log.error("Incorrect system language configuration , please check! ");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(locale);
        return localeResolver;
    }*/
}
