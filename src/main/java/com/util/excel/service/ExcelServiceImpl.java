package com.util.excel.service;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.util.excel.api.ExcelService;
import com.util.excel.object.ExcelModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.util.date.DateUtil.formatOfLocalDate;

/**
 * @Version 1.0
 * @ClassName ExcelServiceImpl
 * @Author jiachenXu
 * @Date 2020/3/6 15:08
 * @Description easyExcel导出工具
 */
@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {

    private static final String UTF8 = "utf-8";

    @Override
    public void export(List<Object> data, Class<? extends BaseRowModel> clazz, ExcelTypeEnum excelTypeEnum,
                         HttpServletResponse response, String fileName) throws IOException {
        String fileType = excelTypeEnum.getValue( );
        fileName = correction(fileName);
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName + getFileName( ) + fileType);
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding(UTF8);
        ServletOutputStream outputStream = response.getOutputStream( );
        ExcelWriter excelWriter = new ExcelWriter(outputStream, excelTypeEnum);
        excelWriter.write(data, new Sheet(1, 0, clazz));
        excelWriter.finish();
        outputStream.flush();
        log.info("Excel导出完成");
    }

    @Override
    public List<? extends BaseRowModel> upload(MultipartFile file, Class<? extends BaseRowModel> clazz, Class<?> dbHandle) throws IOException {
        InputStream inputStream = file.getInputStream( );
        ExcelReadListener<ExcelModel> excelReadListener = new ExcelReadListener<>( );
        ExcelReader reader = new ExcelReader(inputStream, null, excelReadListener);
        reader.read(new Sheet(1, 1, clazz));
        log.info("Excel解析完成");
        return excelReadListener.getData( );
    }

    /**
     * 根据当前时间命名文件名称
     *
     * @return 文件名称
     * @throws
     */
    private static String getFileName() {
        return formatOfLocalDate("yyyy-MM-dd");
    }

    /**
     * 防止文件名称乱码
     *
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    private String correction(String fileName) throws UnsupportedEncodingException {
        return new String(fileName.trim( ).getBytes(UTF8), "iso8859-1");
    }

}
