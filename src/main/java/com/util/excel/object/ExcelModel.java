package com.util.excel.object;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Version 1.0
 * @ClassName ExcelModel
 * @Author jiachenXu
 * @Date 2020/8/29 21:33
 * @Description
 */
@Data
public class ExcelModel extends BaseRowModel implements Serializable {

    private static final long serialVersionUID = -9112265125479169632L;

    @ExcelProperty(value = "主键id", index = 0)
    private Integer id;

    @ExcelProperty(value = "名称", index = 1)
    private String name;

    @ExcelProperty(value = "年龄", index = 2)
    private Integer age;

    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

}
