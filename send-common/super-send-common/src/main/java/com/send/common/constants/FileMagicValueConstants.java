package com.send.common.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
public class FileMagicValueConstants {


    public static final Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {

        FILE_TYPE_MAP.put("jpg", "FFD8FF");
        FILE_TYPE_MAP.put("png", "89504E47");
        FILE_TYPE_MAP.put("gif", "47494638");
        FILE_TYPE_MAP.put("tif", "49492A00");
        FILE_TYPE_MAP.put("bmp", "424D");
        FILE_TYPE_MAP.put("dwg", "41433130");
        FILE_TYPE_MAP.put("ico", "00000100");

        FILE_TYPE_MAP.put("html", "68746D6C3E");
        FILE_TYPE_MAP.put("rtf", "7B5C727466");
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        FILE_TYPE_MAP.put("zip", "504B0304");
        FILE_TYPE_MAP.put("rar", "52617221");
        //Photoshop (psd)
        FILE_TYPE_MAP.put("psd", "38425053");
        //Email [thorough only] (eml)
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");
        //Outlook Express (dbx)
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");
        //Outlook (pst)
        FILE_TYPE_MAP.put("pst", "2142444E");
        //MS docx 或者 xlsx
        FILE_TYPE_MAP.put("xlsx", "504B0304");
        FILE_TYPE_MAP.put("docx", "504B0304");
        //MS Word
        FILE_TYPE_MAP.put("xls", "D0CF11E0");
        //MS Excel 注意：word 和 excel的文件头一样
        FILE_TYPE_MAP.put("doc", "D0CF11E0");
        //MS Access (mdb)
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A");
        //WordPerfect (wpd)
        FILE_TYPE_MAP.put("wpd", "FF575043");
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");
        //Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("pdf", "255044462D312E");
        //Quicken (qdf)
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F");
        //Windows Password (pwl)
        FILE_TYPE_MAP.put("pwl", "E3828596");
        //音频文件
        FILE_TYPE_MAP.put("mp3", "4944330300");
        FILE_TYPE_MAP.put("wav", "57415645");
        //视频文件
        FILE_TYPE_MAP.put("avi", "41564920");
        FILE_TYPE_MAP.put("mp4", "0000002066747970");
        FILE_TYPE_MAP.put("mkv", "1A45DFA3A3428681");
        //Real Audio (ram)
        FILE_TYPE_MAP.put("ram", "2E7261FD");
        //Real Media (rm)
        FILE_TYPE_MAP.put("rm", "2E524D46");
        FILE_TYPE_MAP.put("mpg", "000001BA");
        FILE_TYPE_MAP.put("mov", "6D6F6F76");
        //Windows Media (asf)
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11");
        //MIDI (mid)
        FILE_TYPE_MAP.put("mid", "4D546864");
    }

    public static List<String> findFileTypeByMagic(String magic) {
        if (StringUtils.isBlank(magic)) {
            return Collections.emptyList();
        }
        return FILE_TYPE_MAP.entrySet().stream()
                .filter(entry -> StringUtils.startsWithIgnoreCase(magic, entry.getValue()))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

}
