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
 * @date 2025年04月09日 9:50
 * @description：
 */
@Data
@FieldNameConstants
public class CustomerServiceManagement implements Serializable {
    private static final long serialVersionUID = -3590075703752838610L;

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
     * 客服用户名（必须为11位手机号）
     * 数据库字段：customer_name varchar(20) NOT NULL
     */
    private String customerName;

    /**
     * 客服状态，1:离线状态 2:在线状态
     */
    private Integer status;

    /**
     * 备注信息
     * 数据库字段：remarks varchar(500) NOT NULL
     */
    private String remarks;

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
