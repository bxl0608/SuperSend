package com.send.common;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class BaseInvoke {
    private BaseInvoke() {
    }

    /**
     * 执行脚本,并将结果返回
     *
     * @param commands 入参
     * @return 出参
     */
    public static String invokeScript(String commands) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Process pcs = Runtime.getRuntime().exec(commands);
            buildExecResult(stringBuilder, pcs);
        } catch (IOException e) {
            log.error("BaseInvoke invokeScript happen Exception:", e);
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    private static void buildExecResult(StringBuilder stringBuilder, Process pcs) {
        try (
                BufferedInputStream bufferedInputStream = new BufferedInputStream(pcs.getInputStream());
                InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
            // 输出文本
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("|");
            }
        } catch (IOException e) {
            log.error("BaseInvoke invokeScript inner-try happen Exception:", e);
        }
    }
}
