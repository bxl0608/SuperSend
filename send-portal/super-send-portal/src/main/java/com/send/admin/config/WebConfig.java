package com.send.admin.config;

import com.project.base.i18n.I18nMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Configuration
public class WebConfig {

    @Bean
    public I18nMessage i18nMessage() {
        return new I18nMessage(Locale.SIMPLIFIED_CHINESE);
    }

}
