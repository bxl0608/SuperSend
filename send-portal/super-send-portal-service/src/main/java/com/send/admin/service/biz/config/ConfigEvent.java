package com.send.admin.service.biz.config;


import com.send.model.db.mysql.bo.config.IConfigDetail;
import com.send.model.db.mysql.bo.config.TbConfigBo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Getter
@Setter
public class ConfigEvent extends ApplicationEvent {
    public ConfigEvent(Object source) {
        super(source);
    }

    private TbConfigBo<IConfigDetail> newBo;

    private TbConfigBo<IConfigDetail> oldBo;

    private Integer currentLoginUserId;

    private String currentToken;
}
