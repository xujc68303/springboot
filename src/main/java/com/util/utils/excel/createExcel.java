package com.util.utils.excel;

import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.support.ExcelTypeEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName createExcelByEaysExcel
 * @Author jiachenXu
 * @Date 2020/3/6 15:06
 * @Description easyExcel解析工具
 */
public interface createExcel {

    /**
     * 文件导出为Excel
     *
     * @param data data
     * @param excelTypeEnum 导出格式
     * @param response response
     * @param fileName 文件名称
     */
    void createExcel(List<? extends BaseRowModel> data, ExcelTypeEnum excelTypeEnum,
                     HttpServletResponse response, String fileName) throws IOException;
}
