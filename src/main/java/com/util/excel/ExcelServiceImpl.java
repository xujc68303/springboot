package com.util.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class ExcelServiceImpl implements ExcelService {

    private static final String UTF8 = "utf-8";

    @Override
    public String export(List<? extends BaseRowModel> data, ExcelTypeEnum excelTypeEnum,
                         HttpServletResponse response, String fileName) throws IOException {
        fileName = new String(fileName.trim( ).getBytes(UTF8), "iso-8859-1");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName + getFileName( ));
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding(UTF8);
        ServletOutputStream outputStream = response.getOutputStream( );
        ExcelWriter excelWriter = new ExcelWriter(outputStream, excelTypeEnum, true);
        writeExcelByList(data, excelWriter);
        outputStream.flush( );
        log.info("Excel导出完成");
        return fileName;
    }

    @Override
    public String upload(MultipartFile file) {
        return null;
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
     * 创建多行
     *
     * @param map
     * @param excelWriter
     */
    private static void writeExcelByMap(Map<String, List<? extends BaseRowModel>> map, ExcelWriter excelWriter) {
        int sheetNum = 1;
        for (Map.Entry<String, List<? extends BaseRowModel>> stringListEntry : map.entrySet( )) {
            Sheet sheet = new Sheet(sheetNum, 0, stringListEntry.getValue( ).get(0).getClass( ));
            sheet.setSheetName(stringListEntry.getKey( ));
            excelWriter.write(stringListEntry.getValue( ), sheet);
            excelWriter.finish( );
            sheetNum++;
        }
    }

    /**
     * 创建多行
     *
     * @param list
     * @param excelWriter
     */
    private static void writeExcelByList(List<? extends BaseRowModel> list, ExcelWriter excelWriter) {
        Sheet sheet = new Sheet(1, 0, list.get(0).getClass( ));
        excelWriter.write(list, sheet);
        excelWriter.finish( );
    }

}
