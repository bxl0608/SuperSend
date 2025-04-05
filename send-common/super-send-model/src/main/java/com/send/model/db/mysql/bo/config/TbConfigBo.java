package com.send.model.db.mysql.bo.config;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class TbConfigBo<T extends IConfigDetail> {
    private Integer id;

    private String type;

    private T detail;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}