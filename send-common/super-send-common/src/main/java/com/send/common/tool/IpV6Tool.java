package com.send.common.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class IpV6Tool {
    private static final int BIT_LENGTH = 128;
    /**
     * 2个0
     */
    private static final String ZERO_2 = "00";
    /**
     * 8个0
     */
    private static final String ZERO_8 = "00000000";
    private static final String COLON = ":";
    private static final String DOUBLE_COLON = "::";
    private static final int TOW_SECTION = 2;
    private static final int IP_LENGTH = 128;

    private IpV6Tool() {
    }

    /**
     * 是否为ipv6
     *
     * @param ipv6 入参
     * @return 出参
     */
    public static boolean isIpV6(String ipv6) {
        if (StringUtils.isBlank(ipv6)) {
            return false;
        }
        InetAddressValidator instance = InetAddressValidator.getInstance();
        return instance.isValidInet6Address(ipv6);
    }

    public static boolean isIpV6Subnet(String subnet) {

        if (subnet == null) {
            return false;
        }
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
        return isIpV6(subnetInfos[0]);
    }

    /**
     * 标准化
     * 使用“:”间隔的8段；每段为4位的16进制
     * 1:2a1b:3a:45:a5::71 --> 0001:2a1b:003a:0045:00a5:0000:0000:0071
     *
     * @param ipv6 入参
     */
    public static String standardize2Hex(String ipv6) {
        return standardize2Str(ipv6, COLON, IpV6Tool::hex2Hex);
    }

    /**
     * 标准化
     * 使用“:”间隔的8段；每段为16位的二进制
     * 1:2a1b:3a:45:a5::71 --> 0000000000000001:0010101000011011:0000000000111010:0000000001000101:0000000010100101:0000000000000000:0000000000000000:0000000001110001
     *
     * @param ipv6 入参
     */
    public static String standardize2BinaryWithColon(String ipv6) {
        return standardize2Str(ipv6, COLON, IpV6Tool::hex2Binary);
    }

    /**
     * 标准化
     * 128位2进制字符串
     * 1:2a1b:3a:45:a5::71 --> 00000000000000010010101000011011000000000011101000000000010001010000000010100101000000000000000000000000000000000000000001110001
     *
     * @param ipv6 入参
     */
    public static String standardize2Binary(String ipv6) {
        return standardize2Str(ipv6, null, IpV6Tool::hex2Binary);
    }


    /**
     * 转换为二进制
     *
     * @param ipv6               入参
     * @param splitBy            入参
     * @param byteStringFunction 用于将byte转换为字符串
     * @return 出参
     */
    private static String standardize2Str(String ipv6, String splitBy, UnaryOperator<String> byteStringFunction) {
        if (ipv6 == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String[] hexArray = toHexArray(ipv6);
        for (int i = 0; i < hexArray.length; i++) {
            String str = hexArray[i];
            String apply = byteStringFunction.apply(str);
            builder.append(apply);
            if (StringUtils.isEmpty(splitBy)) {
                continue;
            }
            if (i % 2 == 1 && i < hexArray.length - 1) {
                builder.append(splitBy);
            }
        }
        return builder.toString();
    }

    /**
     * 将IPv6转换为字节数组，长度为16
     *
     * @param ipv6 入参
     * @return 出参
     */
    private static String[] toHexArray(String ipv6) {
        int byteIndex = 0;
        String[] stringArray = new String[16];
        if (ipv6.contains(DOUBLE_COLON)) {
            String[] split = ipv6.split(DOUBLE_COLON);
            String[] firstPart = split[0].split(COLON);
            String[] lastPart = split.length == 1 ? new String[0] : split[1].split(COLON);
            int continuousZeroSection = 8 - firstPart.length - lastPart.length;
            // “::”之前部分
            byteIndex = buildHexArray(byteIndex, stringArray, firstPart);
            // “::”部分
            for (int i = 0; i < continuousZeroSection * 2; i++) {
                stringArray[byteIndex++] = "0";
            }
            // “::”之后部分
            buildHexArray(byteIndex, stringArray, lastPart);
        } else {
            String[] arrays = ipv6.split(COLON);
            buildHexArray(byteIndex, stringArray, arrays);
        }
        return stringArray;
    }

    private static int buildHexArray(int byteIndex, String[] stringArray, String[] strings) {
        for (String strNumbers : strings) {
            int length = strNumbers.length();
            if (length > 2) {
                String high = strNumbers.substring(0, length - 2);
                stringArray[byteIndex++] = high;
                String low = strNumbers.substring(length - 2);
                stringArray[byteIndex++] = low;
            } else {
                stringArray[byteIndex++] = "0";
                stringArray[byteIndex++] = strNumbers;
            }
        }
        return byteIndex;
    }

    /**
     * 将数字转换为长度为2的16进制字符串列表
     * 数字范围0-255，即一个字节
     *
     * @param hexString 入参
     * @return 出参
     */
    private static String hex2Hex(String hexString) {
        hexString = ZERO_2 + hexString;
        return hexString.substring(hexString.length() - ZERO_2.length());
    }


    /**
     * 将数字转换为长度为8的二进制字符串列表
     * 数字范围0-255，即一个字节
     *
     * @param hexString 入参
     * @return 出参
     */
    private static String hex2Binary(String hexString) {
        int intValue = Integer.parseUnsignedInt(hexString, 16);
        String str = Integer.toBinaryString(intValue);
        str = ZERO_8 + str;
        return str.substring(str.length() - ZERO_8.length());
    }

    /**
     * 比较IP大小
     *
     * @param ipV6First  入参
     * @param ipV6Second 入参
     * @return 出参
     */
    public static int compare(String ipV6First, String ipV6Second) {
        String first = standardize2Hex(ipV6First);
        String second = standardize2Hex(ipV6Second);
        return ObjectUtils.compare(first, second);
    }

    /**
     * ipv6是否在指定网段内
     *
     * @param ipV6   入参
     * @param subnet 入参
     * @return 出参
     */
    public static boolean ipInSubnet(String ipV6, String subnet) {
        String[] subnetInfo = subnet.split("/");
        String subnetHost = standardize2Binary(subnetInfo[0]);
        if (subnetHost == null) {
            return false;
        }
        try {
            int mask = Integer.parseInt(subnetInfo[1]);
            String ip128Bits = standardize2Binary(ipV6);
            return subnetHost.regionMatches(0, ip128Bits, 0, mask);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 128位二进制转换位普通ipv6格式
     *
     * @param binary128String 入参
     * @return 出参
     */
    public static String binaryStringToHexIpV6(String binary128String) {
        if (StringUtils.length(binary128String) != BIT_LENGTH) {
            return null;
        }
        final int binaryBit = 4;
        StringBuilder resultBuilder = new StringBuilder();
        // 每四位单独考虑
        StringBuilder sectionBuilder = new StringBuilder();
        int sectionCount = 0;
        // 是否持续为0
        boolean continueZero = true;
        for (int i = 0; i < BIT_LENGTH; i += binaryBit) {

            // 每四位二进制转换为一位16进制
            String substring = binary128String.substring(i, i + binaryBit);
            int intValue = Integer.parseUnsignedInt(substring, 2);
            if (intValue != 0 || !continueZero) {
                String hexString = Integer.toHexString(intValue);
                sectionBuilder.append(hexString);
            }
            // 记录是否持续为0
            continueZero = continueZero && intValue == 0;
            sectionCount++;

            // 字符串分隔符，每16位二进制（4位16进制）后，使用分隔符分割
            if (sectionCount == 4) {
                resultBuilder.append(sectionBuilder.length() == 0 ? "0" : sectionBuilder);

                // 初始化sectionBuilder
                sectionBuilder = new StringBuilder();
                sectionCount = 0;
                continueZero = true;
                if (i < BIT_LENGTH - binaryBit) {
                    resultBuilder.append(COLON);
                }
            }
        }
        return resultBuilder.toString();
    }

    /**
     * 判断两个网段是否重叠
     *
     * @param subnet1 入参
     * @param subnet2 入参
     * @return true=重叠，false=不重叠
     */
    public static boolean twoSubnetOverlap(String subnet1, String subnet2) {
        String[] subnetInfo1 = subnet1.split("/");
        String[] subnetInfo2 = subnet2.split("/");
        int mask1 = Integer.parseInt(subnetInfo1[1]);
        int mask2 = Integer.parseInt(subnetInfo2[1]);
        int minMask = Math.min(mask1, mask2);
        String toBinary1 = standardize2Binary(subnetInfo1[0]);
        String binary1 = toBinary1 == null ? null : toBinary1.substring(0, minMask);
        String toBinary2 = standardize2Binary(subnetInfo2[0]);
        String binary2 = toBinary2 == null ? null : toBinary2.substring(0, minMask);
        return Objects.equals(binary1, binary2);
    }


    /**
     * @param subnet 入参
     * @return 出参
     */
    public static String[] ipScopesOfBinary(String subnet) {
        if (StringUtils.isBlank(subnet) || !isIpV6Subnet(subnet)) {
            return new String[0];
        }
        String[] ipAndMask = subnet.split("/");
        String ipv6 = ipAndMask[0];
        if (StringUtils.isBlank(ipv6)) {
            return new String[0];
        }
        int mask = Integer.parseInt(ipAndMask[1]);
        String binaryHostIp = standardize2Binary(ipv6);
        String prefix = binaryHostIp.substring(0, mask);
        log.info("prefix={}", prefix);
        String startIp = StringUtils.rightPad(prefix, 128, "0");
        String endIp = StringUtils.rightPad(prefix, 128, "1");
        return new String[]{startIp, endIp};
    }

    /**
     * @param subnet 入参
     * @return 出参
     */
    public static String[] ipScopesOfHex(String subnet) {
        String[] startAndEnd = ipScopesOfBinary(subnet);
        if (ArrayUtils.isEmpty(startAndEnd)) {
            return new String[0];
        }
        startAndEnd[0] = binaryStringToHexIpV6(startAndEnd[0]);
        startAndEnd[1] = binaryStringToHexIpV6(startAndEnd[1]);
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
        boolean isIpv6Subnet = isIpV6Subnet(ipOrCidr);
        if (isIpv6Subnet) {
            return !twoSubnetOverlap(ipOrCidr, cidr);
        } else {
            if (!isIpV6(ipOrCidr)) {
                return true;
            }
            return !ipInSubnet(ipOrCidr, cidr);
        }
    }
}
