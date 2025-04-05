package com.send.model.exception;


import com.send.model.i18n.I18nMsgKeyConstant;
import com.project.base.model.exception.ExceptionType;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public enum MasterExceptionEnum implements ExceptionType {

    ERR_KAFKA_SEND_MESSAGE("1001000", "kafka消息发送失败"),

    ERR_USER_OR_PASSWORD("4001000", "用户名或密码错误"),
    ERR_VERIFY_CODE("4001001", "验证码错误"),
    ERR_FORBIDDEN("4001002", "您无权访问该资源"),
    ERR_ACCESS_DENY("4001004", "访问被拒绝, 请登录后重试"),
    ERR_USER_EXPIRE("4001005", "用户已过有效期，请联系管理员进行延长"),
    ERR_FIRST_LOGIN_NEED_CHANGE_PASSWORD("4001006", "首次登录，请修改密码"),
    ERR_PASSWORD_EXPIRE("4001007", "密码过期，请修改密码"),
    /**
     * 超过登录失败次数限制，请稍等{0}分钟后再登录！
     */
    RETRY_COUNT_EXCEED("4001008", I18nMsgKeyConstant.ERR_LOGIN_FAIL_OVER_LIMIT_COUNT),
    LOCKED("4001009", "用户被锁定，请联系管理员解锁！"),

    ERR_SUB_DEVICE_NOT_EXIST("4001010", "子平台信息不存在"),
    ERR_SUB_DEVICE_SYNC_DOING("4001011", "当前子平台已有同步任务在进行中"),
    ERR_SUB_DEVICE_PROXY_PORT_USED("4001012", "代理端口已被占用"),
    ERR_SUB_DEVICE_PROXY_CONFIG("4001013", "配置不合法，nginx无法启动，请检查端口占用情况"),

    ERR_REGISTER_SUB_DEVICE_NAME_EXIST("4001012", "子平台名称已存在"),
    ERR_REGISTER_SUB_DEVICE_FORMAT("4001012", "子平台注册信息格式错误"),
    ERR_CREDIT_IP("4001013", "对不起，您的IP地址没有系统访问权限"),
    ERR_LICENSE_EXPIRED("4001014", "license已过期，请重新申请license并上传"),
    ERR_REGISTER_SUB_DEVICE_COUNT_LIMIT("4001015", "注册子平台数量已达上限，当前无法继续注册"),
    ERR_CALL_SUB_DEVICE_TOKEN_API_FAIL("4001016", "获取子平台认证token失败"),
    /**
     * {0}
     * 以参数内容作为最终的错误信息
     */
    ERR_COMMON("4001999", I18nMsgKeyConstant.ERROR_COMMON),

    NOT_EXIST_ID("4023003", "id不存在"),
    NOT_SUPPORT_QUERY("4023004", "字段不支持查询"),
    NOT_SUPPORT_OPERATOR("4023005", "字段不支持指定运算符"),
    NOT_EXIST_FILE("4023006", "file不存在"),
    NOT_EXIST_FILE_NAME("4023007", "file不存在"),
    NOT_EXIST_AVAILABLE_AGG_FIELDS("4023008", "不存在有效统计字段"),
    NOT_SUPPORT_AGGREGATION("4023009", "字段不支持统计"),
    OVER_RANGE_TIME("4023007", "时间范围超过限制"),
    BEHAVIOR_ERROR("4023002", "behavior格式错误"),

    ERR_CALL_OPEN_API("4000000", "调用OpenApi失败"),
    ERR_NAME_DUPLICATED("4000001", "名称重复"),
    ERR_ARRIVE_LIMIT_NUMBER("4000002", "数量已达上限"),

    ERR_TRACE_ATTACKER_NOT_EXIST("4019001", "攻击者不存在"),
    ERR_TRACE_MARK_DUP("4019002", "标签已存在"),

    INVALID("4000000", I18nMsgKeyConstant.INVALID),
    NOT_NULL("4000001", I18nMsgKeyConstant.NOT_NULL),
    NOT_BLANK("4000002", I18nMsgKeyConstant.NOT_BLANK),
    NOT_EMPTY("4000003", I18nMsgKeyConstant.NOT_EMPTY),
    NOT_EXIST("4000004", I18nMsgKeyConstant.NOT_EXIST),
    ERROR("4000005", I18nMsgKeyConstant.ERROR),
    ERR_DECRYPT("4000006", I18nMsgKeyConstant.ERR_DECRYPT),
    EXIST("4000007", I18nMsgKeyConstant.EXIST),
    /**
     * 无权操作{0}
     */
    NO_PERMISSION_OPERATE("4000008", I18nMsgKeyConstant.NO_PERMISSION_OPERATE),
    /**
     * 文件大小超过限制
     */
    ERR_FILE_SIZE_OVER_LIMIT("4000009", I18nMsgKeyConstant.ERR_FILE_SIZE_OVER_LIMIT),
    /**
     * 文件上传失败
     */
    ERR_FILE_UPLOAD_FAIL("4000010", I18nMsgKeyConstant.ERR_FILE_UPLOAD_FAIL),
    /**
     * {0}不可用
     */
    ERR_NOT_AVAILABLE("4000011", I18nMsgKeyConstant.ERR_NOT_AVAILABLE),

    /**
     * 仪表盘名称重复
     */
    DUPLICATED_PANEL_NAME("4036001", "仪表盘名称重复"),
    /**
     * 仪表盘不存在
     */
    NOT_EXIST_PANEL("4036002", "仪表盘不存在"),
    /**
     * 视图不存在
     */
    NOT_EXIST_PANEL_VIEW("4036003", "视图不存在"),
    /**
     * 视图不存在
     */
    NOT_EXIST_PANEL_LINK("4036004", "视图不存在"),
    /**
     * 管理端主机不存在
     */
    NOT_EXISTS_SERVER_HOST("4036005", "管理端主机不存在"),
    /**
     * 视图重复
     */
    DUPLICATED_PANEL_VIEW("4036006", "视图重复"),
    /**
     * 不能删除默认仪表盘
     */
    CAN_NOT_DELETE_PANEL("4036007", "不能删除默认仪表盘"),

    /**
     * 密码策略复杂度不够，除小写字母之外，至少勾选两项
     */
    CONFIG_PASSWORD_COMPLEXITY("4037001", I18nMsgKeyConstant.CONFIG_PASSWORD_COMPLEXITY),
    /**
     * 长度限制
     */
    MAX_LENGTH_ERROR("4038001", "单个标签不能超过200个字符"),
    /**
     * 类型不存在
     */
    TYPE_NOT_EXIST("4038002", "类型不存在"),
    ;


    private String code;
    private String message;
    private Object[] paramArray;

    MasterExceptionEnum(String code, String message, Object... paramArray) {
        this.code = code;
        this.message = message;
        this.paramArray = paramArray;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Object[] getMessageParamArray() {
        return paramArray;
    }
}
