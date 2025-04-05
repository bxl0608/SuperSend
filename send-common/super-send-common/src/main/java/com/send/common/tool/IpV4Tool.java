package com.send.common.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.function.UnaryOperator;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class IpV4Tool {
    private static final int IP_LENGTH = 32;
    private static final int TOW_SECTION = 2;
    private static final int BIT_LENGTH = 32;
    /**
     * 2个0
     */
    private static final String ZERO_3 = "000";
    /**
     * 8个0
     */
    private static final String ZERO_8 = "00000000";
    /**
     * ipv4的分隔符：用于拼接
     */
    private static final String DOT = ".";
    /**
     * ipv4的分隔符：用于切分
     */
    private static final String DOT_REGEX = "\\.";

    private IpV4Tool() {

    }

    /**
     * 是否为ipv4
     * 注意：对于023.001.255.018格式的IPv4，返回为fale
     *
     * @param ip 入参
     * @return 出参
     */
    public static boolean isIpV4(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        InetAddressValidator instance = InetAddressValidator.getInstance();
        return instance.isValidInet4Address(ip);
    }

    /**
     * 是否为网段
     *
     * @param subnet 入参
     * @return 出参
     */
    public static boolean isIpV4Subnet(String subnet) {
        String[] subnetInfos = subnet.split("/");
        if (subnetInfos.length != TOW_SECTION) {
            return false;
        }
        try {
            int mask = Integer.parseInt(subnetInfos[1]);
            if (mask > IP_LENGTH || mask < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return isIpV4(subnetInfos[0]);
    }

    /**
     * 标准化
     * 使用“.”间隔的4段；每段为3位十进制数
     * 23.1.255.18 --> 023.001.255.018
     *
     * @param ipv4 入参
     */
    public static String standardize2Dec(String ipv4) {
        return standardize2Str(ipv4, DOT, IpV4Tool::dec2StandardDec);
    }

    /**
     * 标准化
     * 使用“.”间隔的4段；每段为8位的二进制
     * 23.1.255.18 --> 00010111.00000001.11111111.00010010
     *
     * @param ipv4 入参
     */
    public static String standardize2BinaryWithDot(String ipv4) {
        return standardize2Str(ipv4, DOT, IpV4Tool::dec2Binary);
    }

    /**
     * 标准化
     * 32位2进制字符串
     * 23.1.255.18 --> 00010111000000011111111100010010
     *
     * @param ipv4 入参
     */
    public static String standardize2Binary(String ipv4) {
        return standardize2Str(ipv4, null, IpV4Tool::dec2Binary);
    }


    /**
     * 转换为二进制
     *
     * @param ipv4               入参
     * @param splitBy            入参
     * @param byteStringFunction 用于将byte转换为字符串
     * @return 出参
     */
    private static String standardize2Str(String ipv4, String splitBy, UnaryOperator<String> byteStringFunction) {
        if (ipv4 == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String[] strings = ipv4.split(DOT_REGEX);
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            String apply = byteStringFunction.apply(str);
            builder.append(apply);
            if (StringUtils.isEmpty(splitBy)) {
                continue;
            }
            if (i < strings.length - 1) {
                builder.append(splitBy);
            }
        }
        return builder.toString();
    }

    /**
     * 将数字转换为长度补全位3
     * 数字范围0-255，即一个字节
     *
     * @param decString 入参
     * @return 出参
     */
    private static String dec2StandardDec(String decString) {
        decString = ZERO_3 + decString;
        return decString.substring(decString.length() - ZERO_3.length());
    }


    /**
     * 将数字转换为长度为8的二进制字符串列表
     * 数字范围0-255，即一个字节
     *
     * @param decString 入参
     * @return 出参
     */
    private static String dec2Binary(String decString) {
        int intValue = Integer.parseUnsignedInt(decString);
        String str = Integer.toBinaryString(intValue);
        str = ZERO_8 + str;
        return str.substring(str.length() - ZERO_8.length());
    }

    /**
     * 比较IP大小
     *
     * @param firstIp  入参
     * @param secondIp 入参
     * @return 出参
     */
    public static int compare(String firstIp, String secondIp) {
        String first = standardize2Dec(firstIp);
        String second = standardize2Dec(secondIp);
        if (first == null) {
            return second == null ? 0 : 1;
        }
        if (second == null) {
            return 1;
        }
        return first.compareToIgnoreCase(second);
    }

    /**
     * ipv6是否在指定网段内
     *
     * @param ip     入参
     * @param subnet 入参
     * @return 出参
     */
    public static boolean ipInSubnet(String ip, String subnet) {
        String[] subnetInfo = subnet.split("/");
        String subnetHost = standardize2Binary(subnetInfo[0]);
        if (subnetHost == null) {
            return false;
        }
        int mask = Integer.parseInt(subnetInfo[1]);
        String ip128Bits = standardize2Binary(ip);
        return subnetHost.regionMatches(0, ip128Bits, 0, mask);
    }

    /**
     * ipv6是否在指定网段内
     *
     * @param ip    入参
     * @param range 入参
     * @return 出参
     */
    public static boolean ipInRange(String ip, String range) {
        String[] rangeInfo = range.split("-");
        Long start = ipToLong(rangeInfo[0]);
        Long end = ipToLong(rangeInfo[1]);
        Long target = ipToLong(ip);
        return start <= target && target <= end;
    }

    /**
     * ip转十进制
     *
     * @param target 入参
     * @return 出参
     */
    public static long ipToLong(String target) {
        String[] arr = target.split(DOT_REGEX);
        long result = 0;
        for (int i = 0; i <= 3; i++) {
            long ip = Long.parseLong(arr[i]);
            result |= ip << ((3 - i) << 3);
        }
        return result;
    }

    /**
     * 32位二进制转换为简化的IP格式
     * 00010111000000011111111100010010 --> 23.1.255.18
     *
     * @param binaryString 入参
     * @return 出参
     */
    public static String binary2Dec(String binaryString) {
        if (StringUtils.length(binaryString) != BIT_LENGTH) {
            return null;
        }
        int binaryBit = 8;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BIT_LENGTH; i += binaryBit) {
            // 每四位二进制转换为一位16进制
            String substring = binaryString.substring(i, i + binaryBit);
            int intValue = Integer.parseUnsignedInt(substring, 2);
            builder.append(intValue);
            if (i < BIT_LENGTH - binaryBit) {
                builder.append(DOT);
            }
        }
        return builder.toString();
    }


    /**
     * ipv4子网是否重叠
     *
     * @param subnet1 入参
     * @param subnet2 入参
     * @return 出参
     */
    public static boolean twoSubnetOverlap(String subnet1, String subnet2) {
        String[] subnetInfo1 = subnet1.split("/");
        String[] subnetInfo2 = subnet2.split("/");
        int mask1 = Integer.parseInt(subnetInfo1[1]);
        int mask2 = Integer.parseInt(subnetInfo2[1]);
        int minMask = Math.min(mask1, mask2);
        String shortHost = mask1 < mask2 ? subnetInfo1[0] : subnetInfo2[0];
        String longHost = mask1 < mask2 ? subnetInfo2[0] : subnetInfo1[0];
        String binaryOfShortHost = standardize2Binary(shortHost);
        if (binaryOfShortHost == null) {
            return false;
        }
        String binaryOfLongHost = standardize2Binary(longHost);
        return binaryOfShortHost.regionMatches(0, binaryOfLongHost, 0, minMask);
    }


    /**
     * @param subnet 入参
     * @return 出参
     */
    public static String[] ipScopesOfBinary(String subnet) {
        String[] ipAndMask = subnet.split("/");
        String ipv6 = ipAndMask[0];
        int mask = Integer.parseInt(ipAndMask[1]);
        String binaryHostIp = standardize2Binary(ipv6);
        if (binaryHostIp == null) {
            return new String[2];
        }
        String prefix = binaryHostIp.substring(0, mask);
        if (prefix == null) {
            return new String[2];
        }
        log.info("prefix={}", prefix);
        String startIp = StringUtils.rightPad(prefix, 32, "0");
        String endIp = StringUtils.rightPad(prefix, 32, "1");
        return new String[]{startIp, endIp};
    }

    /**
     * @param subnet 入参
     * @return 出参
     */
    public static String[] ipScopesOfDec(String subnet) {
        String[] startAndEnd = ipScopesOfBinary(subnet);
        startAndEnd[0] = binary2Dec(startAndEnd[0]);
        startAndEnd[1] = binary2Dec(startAndEnd[1]);
        return startAndEnd;
    }

    /**
     * 检查是否可用
     *
     * @param ipOrCidr ip或网段
     * @param cidr     网段
     * @return 是否被使用 true-可用，false-不可用
     */
    public static Boolean cidrIsNotUsed(String ipOrCidr, String cidr) {
        if (StringUtils.isBlank(ipOrCidr)) {
            return true;
        }
        if (StringUtils.isBlank(cidr)) {
            return false;
        }
        boolean isIpV4Subnet = isIpV4Subnet(ipOrCidr);
        if (isIpV4Subnet) {
            return !twoSubnetOverlap(ipOrCidr, cidr);
        } else {
            if (!isIpV4(ipOrCidr)) {
                return true;
            }
            return !ipInSubnet(ipOrCidr, cidr);
        }
    }

    /**
     * 将数字左侧的0剔除，进行简化
     *
     * @param decString 入参
     * @return 出参
     */
    private static String standardDec2SimpleDec(String decString) {
        String dec = StringUtils.trim(decString);
        if (StringUtils.isBlank(dec)) {
            return dec;
        }
        if (dec.length() == 1) {
            return dec;
        }
        String simple = StringUtils.stripStart(dec, "0");
        if (StringUtils.isBlank(simple) && dec.contains("0")) {
            simple = "0";
        }
        return simple;
    }

    /**
     * 简化IP：023.001.255.018 --> 23.1.255.18
     *
     * @param ip 初始的IP
     * @return 简化的IP
     */
    public static String decIpV42SimpleDec(String ip) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        String[] split = ip.split(DOT_REGEX);
        if (split.length != 4) {
            return null;
        }
        for (int i = 0; i < 4; i++) {
            split[i] = standardDec2SimpleDec(split[i]);
        }
        return StringUtils.join(split, DOT);
    }
}
