package com.send.model.db.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 参数配置
 */
@Data
public class TbConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String type;

    private String detail;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}