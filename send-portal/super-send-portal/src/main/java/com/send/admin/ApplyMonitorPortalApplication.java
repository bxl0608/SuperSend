package com.send.admin;

import com.project.base.mysql.MysqlScanConfiguration;
import com.project.base.web.WebScanConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@SpringBootApplication
@MapperScan({"${mybatis.mapperScan}"})
@Import({WebScanConfiguration.class, MysqlScanConfiguration.class})
public class ApplyMonitorPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplyMonitorPortalApplication.class, args);
    }

}
