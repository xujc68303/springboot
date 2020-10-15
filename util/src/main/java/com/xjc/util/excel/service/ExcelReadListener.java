package com.xjc.util.excel.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName ExcelReadListener
 * @Author jiachenXu
 * @Date 2020/8/29
 * @Description excel解析数据监控
 */
public class ExcelReadListener<ExcelUploadModel> extends AnalysisEventListener<ExcelUploadModel> {

    @NonNull
    private String fileName;

    @NonNull
    private String type;

    private volatile List<ExcelUploadModel> data = new ArrayList<>( );

    public ExcelReadListener() {

    }

    public ExcelReadListener(String fileName, String type) {
        this.fileName = fileName;
        this.type = type;
    }

    @Override
    public void invoke(ExcelUploadModel data, AnalysisContext context) {
        // 此方法可用于数据到达某个数量后 分批落库

    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 此方法可用于最终操作
        // 解析Excel后落库
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ExcelUploadModel> getData() {
        return data;
    }

    @Override
    public String toString() {
        return super.toString( );
    }
}
