package com.send.admin.service.biz.config.picture_config;

import com.send.common.tool.FileVerifyTool;
import com.send.model.exception.MasterExceptionEnum;
import com.send.admin.service.biz.constants.FilePathConstant;
import com.send.admin.service.biz.constants.PictureTypeEnum;
import com.send.admin.service.tool.FileTool;
import com.project.base.model.exception.BusinessException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class PictureConfigService {
    private static final long FILE_SIZE_LIMIT_1M = 1024L * 1024;
    private static final long FILE_SIZE_LIMIT_10M = 1024L * 1024 * 10;

    public void uploadPictures(MultipartHttpServletRequest request) {
        Map<String, MultipartFile> multipartFileMap = request.getFileMap();
        if (MapUtils.isEmpty(multipartFileMap)) {
            throw new BusinessException(MasterExceptionEnum.NOT_EMPTY, "上传文件");
        }
        Map<String, MultipartFile> neededMultipartFileMap = new HashMap<>();
        for (Map.Entry<String, MultipartFile> entry : multipartFileMap.entrySet()) {
            String key = entry.getKey();
            PictureTypeEnum typeEnum = PictureTypeEnum.findEnum(key);
            if (typeEnum == null) {
                continue;
            }
            MultipartFile multipartFile = entry.getValue();
            validatePicture(key, multipartFile);
            neededMultipartFileMap.put(key, multipartFile);
        }
        if (MapUtils.isEmpty(neededMultipartFileMap)) {
            throw new BusinessException(MasterExceptionEnum.NOT_EMPTY, "上传指定的文件");
        }
        for (Map.Entry<String, MultipartFile> entry : neededMultipartFileMap.entrySet()) {
            String type = entry.getKey();

            String directory = findPictureDirectory(type);
            if (directory == null) {
                continue;
            }
            FileUtils.deleteQuietly(new File(directory));
            try {
                FileUtils.forceMkdir(new File(directory));
            } catch (IOException e) {
                throw new BusinessException(MasterExceptionEnum.ERROR, "创建目录：" + directory);
            }
            MultipartFile multipartFile = entry.getValue();
            String filePath = directory + multipartFile.getOriginalFilename();
            FileTool.uploadFile(filePath, multipartFile);
        }
    }

    private void validatePicture(String type, MultipartFile multipartFile) {
        // 文件大小
        long fileSizeLimit = PictureTypeEnum.BACKGROUND.getType().equals(type) ? FILE_SIZE_LIMIT_10M : FILE_SIZE_LIMIT_1M;
        if (multipartFile.getSize() == 0 || multipartFile.getSize() > fileSizeLimit) {
            throw new BusinessException(MasterExceptionEnum.INVALID, type + "文件大小");
        }
        // 文件格式
        if (PictureTypeEnum.TITLE.getType().equals(type)) {
            // 魔数校验：支持ico、png、jpg
            try {
                List<String> fileTypeList = FileVerifyTool.findFileType(multipartFile.getInputStream(), 4);
                if (fileTypeList.stream().noneMatch(s -> StringUtils.equalsAnyIgnoreCase(s, "ico", "png", "jpg"))) {
                    throw new BusinessException(MasterExceptionEnum.INVALID, type + "文件格式");
                }
            } catch (IOException e) {
                throw new BusinessException(MasterExceptionEnum.INVALID, type + "文件格式");
            }
        } else {
            Image image = FileTool.buildImage(multipartFile);
            if (image == null) {
                throw new BusinessException(MasterExceptionEnum.INVALID, type);
            }
        }


    }

    public void downloadPicture(String type, HttpServletResponse response) {
        String directory = findPictureDirectory(type);
        if (directory == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, "类型对应文件");
        }
        File directoryFile = new File(directory);
        if (!directoryFile.isDirectory()) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, "类型对应文件");
        }
        File[] fileArray = directoryFile.listFiles(File::isFile);
        if (fileArray == null || fileArray.length == 0) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, "类型对应文件");
        }

        File file = fileArray[0];
        FileTool.download(file.getAbsolutePath(), file.getName(), response);
    }

    private String findPictureDirectory(String type) {
        PictureTypeEnum typeEnum = PictureTypeEnum.findEnum(type);
        if (typeEnum == null) {
            throw new BusinessException(MasterExceptionEnum.INVALID, "类型");
        }
        switch (typeEnum) {
            case BACKGROUND:
                return FilePathConstant.SYSTEM_BACKGROUND;
            case LOGO_LOGIN:
                return FilePathConstant.SYSTEM_LOGIN_LOGO;
            case LOGO_MAIN:
                return FilePathConstant.SYSTEM_MAIN_LOGO;
            case TITLE:
                return FilePathConstant.SYSTEM_ICO;
            default:
                return null;
        }
    }
}
