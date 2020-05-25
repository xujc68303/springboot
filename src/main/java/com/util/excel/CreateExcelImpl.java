package com.util.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Version 1.0
 * @ClassName CreateExcelImpl
 * @Author jiachenXu
 * @Date 2020/3/6 15:08
 * @Description easyExcel导出工具
 */
@Slf4j
public class CreateExcelImpl implements ExcelService {

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

    @Override
    public void export(HttpServletResponse response, String fileName) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("xjc");
        //service

        // 表全部字段注释可以通过sql查询出来
        String[] headers = {"主键", "创建时间"};

        HSSFRow row = sheet.createRow(0);
        for(int i =0; i < headers.length ; i++){
            HSSFCell hssfCell = row.createCell(i);
            hssfCell.setCellValue(new HSSFRichTextString(headers[i]));
        }
        AtomicInteger atomicInteger = new AtomicInteger(1);
        HSSFRow hssfRow = sheet.createRow(atomicInteger.get());
        hssfRow.createCell(0).setCellValue(new HSSFRichTextString("通过sql获取的字段"));
        // ++
        atomicInteger.getAndIncrement();

        response.setHeader("Content-disposition", "attachment; filename" + getFileName() + "_" + fileName + ".xls");
        response.setContentType("application/force-download");
        response.setCharacterEncoding("utf-8");
        OutputStream outputStream  = response.getOutputStream();
        workbook.write(outputStream);
        response.flushBuffer();
        outputStream.close();
    }


    /**
     * 根据当前时间命名文件名称
     *
     * @return 文件名称
     * @throws UnsupportedEncodingException
     */
    private static String getFileName() throws UnsupportedEncodingException {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(localDateTime).getBytes("utf-8").toString( );
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
