package com.send.admin.service.bo.reply;

import com.project.base.model.pagination.PageRequest;
import com.project.base.validation.annotation.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月09日 20:49
 * @description：
 */
@Data
public class PageQueryQuickReplyCustomerRequestBo {

    @ApiModelProperty("分页")
    @Valid
    @NotNull(message = "分页对象不能为空")
    private PageRequest page;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;


    /**
     * 快捷回复内容
     */
    private String content;
}
