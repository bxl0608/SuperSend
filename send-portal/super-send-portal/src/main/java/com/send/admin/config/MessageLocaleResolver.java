package com.send.admin.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.project.base.common.json.JsonTool;
import com.send.dao.repository.TbConfigDao;
import com.send.model.db.mysql.TbConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

import static com.send.admin.config.WebConfig.LOCALE_ZH_CN;
import static com.send.admin.service.biz.config.I18nService.LOCALE_EN_US;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月10日 16:40
 * @description：
 */
@Slf4j
public class MessageLocaleResolver implements LocaleResolver {
    @Autowired
    private TbConfigDao tbConfigDao;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
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
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    }
}