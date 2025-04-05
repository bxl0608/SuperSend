package com.send.admin.service.tool;

import com.send.model.exception.MasterExceptionEnum;
import com.project.base.model.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
public class FileTool {
    public static final String DISPOSITION_HEADER_NAME = "Content-Disposition";
    /**
     * excel后缀
     */
    public static final String EXCEL_SUFFIX_XLS = "xls";
    /**
     * excel后缀
     */
    public static final String EXCEL_SUFFIX_XLSX = "xlsx";

    private FileTool() {
    }

    /**
     * 判断单元格数据的类型
     *
     * @param cell 入参
     * @return 出参
     */
    public static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String strCell;
        switch (cell.getCellType()) {
            case STRING:
                strCell = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        strCell = format.format(date);
                    } catch (Exception e) {
                        strCell = cell.getStringCellValue();
                        log.warn("", e);
                    }
                    break;
                } else {
                    DecimalFormat df = new DecimalFormat("0");
                    strCell = df.format(cell.getNumericCellValue());
                    break;
                }

            case BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
        }
        if (StringUtils.isEmpty(strCell)) {
            return "";
        }
        return strCell;
    }

    /**
     * 将文件中的数据封装到map中
     *
     * @param sheetNum 需要导入的sheet
     * @param file     需要导入的文件
     * @param clazz    需要返回的class类型
     * @return 出参
     * @throws Exception error
     */
    @SneakyThrows
    public static <T> List<T> readExcelAndConvertToEntity(int sheetNum, MultipartFile file, Class<T> clazz) {
        //数据初始化
        List<T> list = new ArrayList<>();
        //获取文件名
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return list;
        }
        try (InputStream input = file.getInputStream()) {
            Workbook wb = buildWorkbook(filename, input);
            if (wb == null || null == wb.getSheetAt(sheetNum) || wb.getSheetAt(sheetNum).getLastRowNum() < 1) {
                log.error("FileUtil readExcel columnNum or rowNum is Error!");
                return list;
            }
            Sheet sheet = wb.getSheetAt(sheetNum);
            log.debug("导入的列数：{}", sheet.getRow(0).getPhysicalNumberOfCells());
            //获取标题行
            Map<Integer, String> titleMap = buildHeaderRow(sheet);
            // 读取并处理非标题行数据
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                handlerRowData(clazz, list, sheet, titleMap, rowIndex);
            }
        }
        return list;
    }

    private static <T> void handlerRowData(Class<T> clazz, List<T> list, Sheet sheet, Map<Integer, String> titleMap, int r)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        Row row = sheet.getRow(r);
        if (row == null) {
            return;
        }
        String className = clazz.getName();
        T obj = clazz.newInstance();
        int cells = row.getLastCellNum();
        int nullCellNum = 0;
        for (Integer c = 0; c < cells; c++) {
            Cell cell = row.getCell(c.shortValue());
            String value = cell == null ? "" : FileTool.getStringCellValue(cell);

            if (StringUtils.isBlank(value)) {
                nullCellNum++;
            }
            if (nullCellNum == cells) {
                break;
            }
            value = StringUtils.trim(value);
            //获取需要反射的类的域值
            Field declaredField = Class.forName(className).getDeclaredField(titleMap.get(c));
            Object fieldValue = value;
            if (Byte.class == declaredField.getType()) {
                fieldValue = Byte.valueOf(value);
            } else if (Integer.class == declaredField.getType()) {
                fieldValue = Integer.valueOf(value);
            }
            Field field = ReflectionUtils.findField(obj.getClass(), titleMap.get(c));
            ReflectionUtils.setField(field, obj, fieldValue);
        }
        list.add(obj);
    }

    /**
     * 根据文件后缀构建workbook
     *
     * @param filename 入参
     * @param input    入参
     * @return 出参
     * @throws IOException error
     */
    public static Workbook buildWorkbook(String filename, InputStream input) throws IOException {
        if (filename.endsWith(EXCEL_SUFFIX_XLS)) {
            return new HSSFWorkbook(input);
        }
        if (filename.endsWith(EXCEL_SUFFIX_XLSX)) {
            return new XSSFWorkbook(input);
        }
        return null;
    }

    /**
     * 获取标题行的位置与title的映射关系
     *
     * @param sheet 入参
     * @return 出参
     */
    private static Map<Integer, String> buildHeaderRow(Sheet sheet) {
        Row titleRow = sheet.getRow(0);
        int titleCells = titleRow.getLastCellNum();
        //存储标题和下表的map
        Map<Integer, String> titleMap = new HashedMap<>(titleCells);
        for (Integer c = 0; c < titleCells; c++) {
            Cell cell = titleRow.getCell(c.shortValue());
            String title = FileTool.getStringCellValue(cell);
            titleMap.put(c, title);
        }
        return titleMap;
    }

    /**
     * 信息导出类
     *
     * @param response   响应
     * @param fileName   文件名
     * @param columnList 每列的标题名
     * @param dataList   导出的数据
     */
    public static void writeToExcel(HttpServletResponse response, String fileName, List<String> columnList, List<List<String>> dataList) {
        //声明输出流
        OutputStream os = null;
        //设置响应头
        setResponseHeader(response, fileName);
        //内存中保留1000条数据，以免内存溢出，其余写入硬盘
        try (SXSSFWorkbook wb = new SXSSFWorkbook(1000);) {
            //获取输出流
            os = response.getOutputStream();
            //获取该工作区的第一个sheet
            Sheet sheet1 = wb.createSheet("sheet1");
            int excelRow = 0;
            //创建标题行
            Row titleRow = sheet1.createRow(excelRow++);
            for (int i = 0; i < columnList.size(); i++) {
                //创建该行下的每一列，并写入标题数据
                Cell cell = titleRow.createCell(i);
                cell.setCellValue(columnList.get(i));
            }
            //设置内容行
            if (dataList != null && !dataList.isEmpty()) {
                //外层for循环创建行
                for (int i = 0; i < dataList.size(); i++) {
                    Row dataRow = sheet1.createRow(excelRow++);
                    //内层for循环创建每行对应的列，并赋值
                    //由于多了一列序号列所以内层循环从-1开始
                    for (int j = 0; j < dataList.get(0).size(); j++) {
                        Cell cell = dataRow.createCell(j);
                        //将数据库中读取到的数据依次赋值
                        cell.setCellValue(dataList.get(i).get(j));
                    }
                }
            }
            //将整理好的excel数据写入流中
            wb.write(os);
        } catch (IOException e) {
            log.warn("", e);
        } finally {
            try {
                // 关闭输出流
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.warn("", e);
            }
        }
    }

    /**
     * 设置浏览器下载响应头
     *
     * @param response 入参
     * @param fileName 入参
     */
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            fileName = new String(fileName.getBytes(), StandardCharsets.UTF_8);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader(DISPOSITION_HEADER_NAME, "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片
     *
     * @param imageFile 入参
     * @return 出参
     */
    public static boolean isImage(MultipartFile imageFile) {
        try {
            Image img = ImageIO.read(imageFile.getInputStream());
            return img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片
     *
     * @param imageFile 入参
     * @return 出参
     */
    public static Image buildImage(MultipartFile imageFile) {
        try {
            Image img = ImageIO.read(imageFile.getInputStream());
            if (img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0) {
                return img;
            }
        } catch (Exception e) {
            log.debug("isImage", e);
        }
        return null;
    }

    /**
     * 将文件上传到指定路径
     *
     * @param path 入参
     * @param file 入参
     */
    public static void uploadFile(String path, MultipartFile file) {
        try (
                BufferedInputStream in = new BufferedInputStream(file.getInputStream());
                FileOutputStream out = new FileOutputStream(path);
                BufferedOutputStream output = new BufferedOutputStream(out)) {
            IOUtils.copy(in, output);
        } catch (Exception e) {
            log.error("[FileUtil] uploadFile error", e);
            throw new BusinessException(MasterExceptionEnum.ERR_FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 读取图片->string
     *
     * @param path   入参
     * @param format 入参
     * @return 出参
     */
    public static String readImage(String path, String format) {
        try {
            File file = new File(path);
            BufferedImage image = ImageIO.read(file);
            return imgToBase64String(image, format);
        } catch (IOException e) {
            log.error("[FileUtil] readImage error", e);
            return null;
        }
    }

    /**
     * 将图片转成文件流
     *
     * @param img    入参
     * @param format 入参
     * @return 出参
     * @throws IOException error
     */
    public static String imgToBase64String(RenderedImage img, String format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, format, Base64.getEncoder().wrap(os));
        return os.toString(StandardCharsets.ISO_8859_1.name());
    }

    /**
     * 将图片放入response
     *
     * @param path     入参
     * @param response 入参
     * @return 出参
     */
    public static void readImage(String path, HttpServletResponse response) {
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(path);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("[FileUtil] readImage error", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("[FileUtil] readImage error", e);
                }
            }
        }
    }

    /**
     * 下载本地服务器文件
     *
     * @param filepath 文件路径
     * @param response 入参
     */
    @SneakyThrows
    public static void download(String filepath, String fileName, HttpServletResponse response) {
        File file = new File(filepath);

        if (StringUtils.isBlank(fileName)) {
            fileName = file.getName();
        }
        if (file.exists()) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

            try (
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

                byte[] buffer = new byte[1024];
                int index;
                while (-1 != (index = bis.read(buffer))) {
                    bos.write(buffer, 0, index);
                }
                response.flushBuffer();
            } catch (Exception e) {
                log.warn("", e);
            }
        } else {
            log.warn("FileUtil download failure:[{}]不存在", filepath);
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_FILE);
        }
    }

    /**
     * 将文件以zip的方式下载
     *
     * @param filePathList 入参
     * @param response     入参
     */
    @SneakyThrows
    public static void download(List<String> filePathList, String packageName, HttpServletResponse response) {
        //压缩
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
            //设置响应头
            setResponseHeader(response, packageName + ".zip");
            filePathList.forEach(filePath -> {
                File file = new File(filePath);
                zipFile(file, out);
            });
        }
    }

    /**
     * @param inputFile    入参
     * @param outputStream 入参
     */
    public static void zipFile(File inputFile, ZipOutputStream outputStream) {
        if (inputFile.exists() && inputFile.isFile()) {
            zipFile(inputFile, inputFile.getName(), outputStream);
        }
    }

    /**
     * @param inputFile    入参
     * @param outputStream 入参
     */
    public static void zipFile(File inputFile, String fileNameInZip, ZipOutputStream outputStream) {
        if (inputFile.exists() && inputFile.isFile()) {
            try (FileInputStream fis = new FileInputStream(inputFile)) {
                String fileName = StringUtils.isNotBlank(fileNameInZip) ? fileNameInZip : inputFile.getName();
                outputStream.putNextEntry(new ZipEntry(fileName));
                //声明文件集合用于存放文件
                byte[] buffer = new byte[1024];
                int len;
                // 读入需要下载的文件的内容，打包到zip文件
                while ((len = fis.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.closeEntry();
            } catch (Exception e) {
                log.error("zipFile write fail", e);
            }
        }
    }

    /**
     * 导出txt文件
     *
     * @param response 入参
     * @param shortTxt 导出的字符串
     * @return 出参
     */
    public static void exportTxt(HttpServletResponse response, String shortTxt, String name) {
        BufferedOutputStream buff = null;
        ServletOutputStream outStr = null;
        try {
            response.setCharacterEncoding("utf-8");
            //设置响应的内容类型
            response.setContentType("application/octet-stream");
            //设置文件的名称和格式
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8") + ".txt");
            outStr = response.getOutputStream();
            buff = new BufferedOutputStream(outStr);
            buff.write(shortTxt.getBytes("UTF-8"));
            buff.flush();
            buff.close();
        } catch (Exception e) {
            log.error("[FileUtil] exportTxt error:{}", e);
        } finally {
            try {
                buff.close();
                outStr.close();
            } catch (Exception e) {
                log.error("[FileUtil] exportTxt error:{}", e);
            }
        }
    }

    public static String imageToBase64(String path, String imageType) {
        try {
            BufferedImage bufferedImage;
            File image = new File(path);
            bufferedImage = ImageIO.read(image);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, imageType, outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("[FileUtil] imageToBase64 error:{}", e);
        }
        return null;
    }

    /**
     * 判断文件大小
     *
     * @param len  文件长度
     * @param size 限制大小
     * @param unit 限制单位（B,K,M,G）
     * @return 出参
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if ("K".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if ("M".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if ("G".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }


    /**
     * 检测当前文件的编码格式
     *
     * @param file    入参
     * @param charset 入参
     * @return 出参
     */
    private static boolean detectionCharset(File file, Charset charset) {
        try (ZipFile ignored = new ZipFile(file, charset)) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成文件.
     *
     * @param content  文件内容
     * @param filepath 文件路径
     * @param filename 文件名字
     */
    public static String generateFile(String content, String filepath, String filename) {
        File dir = new File(filepath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String pathname = filepath + File.separator + filename;
        File file = new File(pathname);
        if (file.exists()) {
            file.delete();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(pathname));
            out.write(content);
            out.close();
        } catch (IOException e) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return pathname;
    }

    public static void toZip(List<String> srcDir, String outDir,
                             boolean keepDirStructure) throws Exception {

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(outDir)))) {
            List<File> sourceFileList = new ArrayList<>(srcDir.size());
            for (String dir : srcDir) {
                File sourceFile = new File(dir);
                sourceFileList.add(sourceFile);
            }
            compress(sourceFileList, zos, keepDirStructure);
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception 异常
     */
    private static void compress(File sourceFile, ZipOutputStream zos,
                                 String name, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[2 * 1024];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (keepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }

            } else {
                for (File file : listFiles) {
                    if (keepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(),
                                keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }

                }
            }
        }
    }

    private static void compress(List<File> sourceFileList,
                                 ZipOutputStream zos, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[2 * 1024];
        for (File sourceFile : sourceFileList) {
            String name = sourceFile.getName();
            if (sourceFile.isFile()) {
                zos.putNextEntry(new ZipEntry(name));
                int len;
                FileInputStream in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            } else {
                File[] listFiles = sourceFile.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    if (keepDirStructure) {
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        zos.closeEntry();
                    }

                } else {
                    for (File file : listFiles) {
                        if (keepDirStructure) {
                            compress(file, zos, name + "/" + file.getName(),
                                    keepDirStructure);
                        } else {
                            compress(file, zos, file.getName(),
                                    keepDirStructure);
                        }

                    }
                }
            }
        }
    }
}
