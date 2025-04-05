package com.send.common.tool;

import com.send.common.constants.FileMagicValueConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class FileVerifyTool {

    /**
     * 文件校验
     *
     * @param inputStream （通过getInputStream的方式获取，避免不可重复读）
     * @return 出参
     */
    public static List<String> findFileType(InputStream inputStream, int magicSize) {

        try {
            //读取字节文件中的前magicSize个字节
            byte[] b = new byte[magicSize];
            int read = inputStream.read(b);
            if (read == -1) {
                return Collections.emptyList();
            }
            String hexStringByByte = getHexStringByByte(b);
            //文件类型
            return FileMagicValueConstants.findFileTypeByMagic(hexStringByByte);
        } catch (Exception e) {
            log.warn("findFileType failed", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return Collections.emptyList();
    }

    /**
     * 文件校验
     *
     * @param inputStream （通过getInputStream的方式获取，避免不可重复读）
     * @return 出参
     * @throws IOException error
     */
    public static boolean isValidFile(InputStream inputStream) throws IOException {

        boolean result = false;
        //读取字节文件中的前50个字节
        byte[] b = new byte[50];
        inputStream.read(b);
        //文件类型
        String fileMagicValue = getFileTypeByStream(b);
        if (StringUtils.isNotBlank(fileMagicValue)) {
            result = true;
        }
        inputStream.close();
        return result;
    }

    /**
     * 将字节数组转成十六进制字符串
     *
     * @param b 入参
     * @return 出参
     */
    private static String getFileTypeByStream(byte[] b) {
        String filetypeHex = String.valueOf(getHexStringByByte(b));
        Iterator<Map.Entry<String, String>> entryIterator = FileMagicValueConstants.FILE_TYPE_MAP.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, String> entry = entryIterator.next();
            String fileTypeHexValue = entry.getValue();
            //如果是以该字节码为开头,则属于该类型文件
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 将字节转为十六进制数据
     *
     * @param b 入参
     * @return 出参
     */
    private static String getHexStringByByte(byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        if (b == null || b.length <= 0) {
            return null;
        }
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


}
