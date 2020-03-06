package com.util.utils.excelutil;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Version 1.0
 * @ClassName createExcelByEaysExcelImpl
 * @Author xujiachen
 * @Date 2020/3/6 15:08
 * @Description easyExcel导出工具
 */
@Slf4j
public class createExcelByEaysExcelImpl implements createExcelByEaysExcel {

    @Override
    public void createExcel(List<? extends BaseRowModel> list, ExcelTypeEnum excelTypeEnum,
                            HttpServletResponse response, String fileName) throws IOException {

        fileName = fileName.trim( ).getBytes("utf-8").toString( );

        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName + getFileName( ));
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        ServletOutputStream outputStream = response.getOutputStream( );

        ExcelWriter excelWriter = new ExcelWriter(outputStream, excelTypeEnum, true);

        // writeExcelByMap()
        // writeExcelByList();
        log.info("Excel导出完成");
    }

    /**
     * 根据当前时间命名文件名称
     *
     * @return 文件名称
     * @throws UnsupportedEncodingException
     */
    private static String getFileName() throws UnsupportedEncodingException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date( )).getBytes("utf-8").toString( );
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
        sheet.setSheetName("sheetName");
        excelWriter.write(list, sheet);
        excelWriter.finish( );
    }

}
