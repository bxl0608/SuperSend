package com.send.admin.service.bo.reply;

import com.project.base.validation.annotation.Length;
import com.project.base.validation.annotation.NotBlank;
import com.project.base.validation.annotation.NotNull;
import lombok.Data;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-09 21:27
 * @Description:
 * @Company: Information Technology Company
 */
@Data
public class UpdateQuickReplyCustomerRequestBo {

    @NotNull(message = "素材id不能为空")
    private String id;
    /**
     * 快捷回复内容
     */
    @NotBlank(message = "快捷回复内容不能为空")
    @Length(max = 300, message = "快捷回复内容长度不能超过300个字符")
    private String content;

    /**
     * 快捷回复类型（默认值：1-文字类型）
     */
    private Integer type;

}