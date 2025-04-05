package com.send.model.i18n;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public class I18nMsgKeyConstant {
    // 消息模板：需要国际化
    public static final String INVALID = "{invalid}";
    public static final String NOT_NULL = "{not.null}";
    public static final String NOT_BLANK = "{not.blank}";
    public static final String NOT_EMPTY = "{not.empty}";
    public static final String NOT_EXIST = "{not.exist}";

    public static final String ERROR = "{error}";
    public static final String ERROR_COMMON = "{error.placeholder}";
    public static final String ERR_DECRYPT = "{err.decrypt}";

    public static final String EXIST = "{err.exist}";
    /**
     * 无权操作{0}
     */
    public static final String NO_PERMISSION_OPERATE = "{err.no.permission.operate}";
    public static final String PARAM_USER = "{param.user}";

    //仪表盘名称重复
    public static final String PANEL_NAME_REPEAT = "{err.panel.name.repeat}";
    //仪表盘不存在
    public static final String PANEL = "{err.panel}";
    //视图不存在
    public static final String PANEL_VIEW = "{err.panel.view}";
    //视图关联信息不存在
    public static final String PANEL_LINK = "{err.panel.link}";
    //管理端主机不存在
    public static final String PANEL_SERVER = "{err.server}";
    //仪表盘视图重复
    public static final String PANEL_VIEW_REPEAT = "{err.panel.view.repeat}";
    //不能删除默认仪表盘
    public static final String PANEL_DELETE = "{err.panel.delete}";


    public static final String LICENSE_ERROR_EXPIRE_DATE = "{license.error.expire.date}";
    public static final String LICENSE_ERROR_PRODUCT_NAME = "{license.error.product.name}";
    public static final String LICENSE_ERROR_PRODUCT_VERSION = "{license.error.product.version}";
    public static final String LICENSE_ERROR_MACHINE_CODE = "{license.error.machine.code}";
    public static final String LICENSE_ERROR_CUSTOMER_NAME_EMPTY = "{license.error.customer.name.empty}";
    public static final String LICENSE_ERROR_PRODUCT_USAGE = "{license.error.product.usage}";
    public static final String LICENSE_ERROR_PRODUCT_MODULE = "{license.error.product.module}";
    public static final String LICENSE_ERROR_START_DATE = "{license.error.start.date}";
    public static final String LICENSE_ERROR_MG_LIMIT = "{license.error.mg.limit}";
    public static final String LICENSE_ERROR_INFO = "{license.error.info}";
    public static final String LICENSE_ERROR_NOT_VERIFIED = "{license.error.not.verified}";
    public static final String LICENSE_ERROR_UNKNOWN = "{license.error.unknown}";

    public static final String CONFIG_PASSWORD_COMPLEXITY = "{err.config.password.complexity}";
    /**
     * 文件大小超过限制
     */
    public static final String ERR_FILE_SIZE_OVER_LIMIT = "{err.file.size.over.limit}";
    /**
     * 文件上传失败
     */
    public static final String ERR_FILE_UPLOAD_FAIL = "{err.file.upload.fail}";
    /**
     * 超过登录失败次数限制，请稍等{0}分钟后再登录！
     */
    public static final String ERR_LOGIN_FAIL_OVER_LIMIT_COUNT = "{err.login.fail.over.limit.count}";
    /**
     * {0}不可用
     */
    public static final String ERR_NOT_AVAILABLE = "{err.not.available}";
}
