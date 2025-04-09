package com.send.model.db.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月07日 19:07
 * @description：
 */
@Data
@FieldNameConstants
public class AccountDetail  implements Serializable {
    private static final long serialVersionUID = -3590075703752838610L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;
    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 操作人用户名
     */
    private String operatorUsername;

    /**
     * 1:系统充值,2:用户消费
     */
    private Integer changeType;

    /**
     * 余额
     */
    private String changeAmount;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 变动前余额
     */
    private BigDecimal changeBeforeAmount;

    /**
     * 变动后余额
     */
    private BigDecimal changeAfterAmount;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
