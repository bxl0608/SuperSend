package com.send.admin.service.biz.constants;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public class LicenseConstants {
    /**
     * 产品名称
     */
    public static final String DDS = "DDS";
    /**
     * 错误码 -1
     */
    public static final Integer MINUS_ONE = -1;
    /**
     * 错误码 -2
     */
    public static final Integer MINUS_TWO = -2;
    /**
     * 错误码 -3
     */
    public static final Integer MINUS_THREE = -3;
    /**
     * 错误码 -4
     */
    public static final Integer MINUS_FOUR = -4;
    /**
     * 错误码 -5
     */
    public static final Integer MINUS_FIVE = -5;
    /**
     * 错误码 -6
     */
    public static final Integer MINUS_SIX = -6;
    /**
     * 错误码 -7
     */
    public static final Integer MINUS_SEVEN = -7;
    /**
     * 错误码 -8
     */
    public static final Integer MINUS_EIGHT = -8;
    /**
     * 错误码 -9
     */
    public static final Integer MINUS_NINE = -9;
    /**
     * 错误码 -10
     */
    public static final Integer MINUS_TEN = -10;
    /**
     * 错误码 -100
     */
    public static final Integer MINUS_HUNDRED = -100;
    /**
     * 主机数量正则
     */
    public static final String HOST_REGEX = "agent_limit:(?<limit>\\d+)";
    /**
     * 判断licence是否支持总控
     * 需要适配agent_limit和mg_limit
     */
    public static final String MG_LIMIT_REGEX = "mg_limit:(?<mgLimit>\\d+)";
    /**
     * 版本号正则
     */
    public static final String VERSION_REGEX = "2.[0-9].[0-9].[0-9]{6}";
    /**
     * 产品code
     */
    public static final String DDS_WORD = "Idss@1207";
    /**
     * license参数数量
     */
    public static final Integer ITEM_NUM = 9;
}
