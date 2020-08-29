package com.util.excel.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName ExcelReadListener
 * @Author jiachenXu
 * @Date 2020/8/29 21:19
 * @Description excel解析数据监控
 */
@Data
public class ExcelReadListener<ExcelModel> extends AnalysisEventListener<ExcelModel> {

    private String fileName;

    private String type;

    private volatile List<ExcelModel> data = new ArrayList<>();

    public ExcelReadListener(){

    }

    @Override
    public void invoke(ExcelModel data, AnalysisContext context) {
        // 此方法可用于数据到达某个数量后 分批落库

    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 此方法可用于最终操作
        // 解析Excel后落库
    }
}
