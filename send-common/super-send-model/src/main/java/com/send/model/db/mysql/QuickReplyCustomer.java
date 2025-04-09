package com.send.model.db.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月09日 19:50
 * @description：
 */
@Data
@FieldNameConstants
public class QuickReplyCustomer implements Serializable {
    private static final long serialVersionUID = -5126222096566343372L;

    /**
     * 主键ID（自增长）
     * 数据库字段：id int(11) NOT NULL AUTO_INCREMENT
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     * 数据库字段：user_id int(11) NOT NULL
     */
    private Integer userId;

    /**
     * 用户名
     * 数据库字段：user_name varchar(20) NOT NULL
     */
    private String userName;

    /**
     * 快捷回复内容
     * 数据库字段：content varchar(300) NOT NULL
     */
    private String content;

    /**
     * 快捷回复类型（默认值：1-文字类型）
     * 数据库字段：type integer NOT NULL DEFAULT 1
     */
    private Integer type;

    /**
     * 创建时间（自动设置为当前时间）
     * 数据库字段：create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（修改时自动更新为当前时间）
     * 数据库字段：update_time datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
     */
    private LocalDateTime updateTime;
}