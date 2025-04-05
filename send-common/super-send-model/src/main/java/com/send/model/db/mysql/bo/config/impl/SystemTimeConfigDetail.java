package com.send.model.db.mysql.bo.config.impl;

import com.send.model.db.mysql.bo.config.IConfigDetail;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class SystemTimeConfigDetail implements IConfigDetail {

    private Boolean networkEnable;

    private String ntpServerAddress;

    private LocalDateTime systemTime;

    private TimeZone timeZone;
}
