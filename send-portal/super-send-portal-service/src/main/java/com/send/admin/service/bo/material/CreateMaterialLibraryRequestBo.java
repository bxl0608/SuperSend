package com.send.admin.service.bo.material;

import com.project.base.validation.annotation.Length;
import com.project.base.validation.annotation.NotBlank;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-09 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class CreateMaterialLibraryRequestBo {

    /**
     * 素材名称
     * 数据库字段：name varchar(50) NOT NULL
     */
    @NotBlank(message = "素材名称不能为空")
    @Length(max = 50, message = "素材名称长度不能超过50个字符")
    private String name;

    /**
     * 素材内容
     * 数据库字段：content varchar(300) NOT NULL
     */
    @NotBlank(message = "素材内容不能为空")
    @Length(max = 300, message = "素材内容长度不能超过300个字符")
    private String content;

    /**
     * 素材类型（默认值：1-文字类型）
     * 数据库字段：type integer NOT NULL DEFAULT 1
     */
    private Integer type = 1;

    /**
     * 备注信息
     * 数据库字段：remarks varchar(500) NOT NULL
     */
    @Length(max = 500, message = "素材备注长度不能超过500个字符")
    private String remarks;

}