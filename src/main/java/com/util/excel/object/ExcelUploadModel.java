package com.util.excel.object;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * @Version 1.0
 * @ClassName ExcelUploadModel
 * @Author jiachenXu
 * @Date 2020/8/29 21:33
 * @Description 解析excel映射到数据表 模型
 */
public class ExcelUploadModel extends BaseRowModel implements Serializable {

    private static final long serialVersionUID = -9112265125479169632L;

    @ExcelProperty(value = "主键id", index = 0)
    private Integer id;

    @ExcelProperty(value = "名称", index = 1)
    private String name;

    @ExcelProperty(value = "年龄", index = 2)
    private Integer age;

    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

    public ExcelUploadModel() {
    }

    public ExcelUploadModel(Integer id, String name, Integer age, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return super.toString( );
    }
}
