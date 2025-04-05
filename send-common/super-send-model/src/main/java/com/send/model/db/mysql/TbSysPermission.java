package com.send.model.db.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
@FieldNameConstants
public class TbSysPermission implements Serializable {

    private static final long serialVersionUID = -1528688237333248921L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Integer id;

    /**
     * 父菜单id
     */
    private Integer parentId;

    /**
     * 0 菜单， 1 接口
     */
    private Integer type;

    /**
     * uri权限字符串
     */
    private String uri;

    /**
     * 菜单排序字段
     */
    private Integer seq;

    /**
     * 前端路由
     */
    private String frontRouter;

    /**
     * 菜单默认显示名称
     */
    private String frontMenuName;

    /**
     * 菜单图标
     */
    private String frontIcon;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public TbSysPermission() {
    }
}
