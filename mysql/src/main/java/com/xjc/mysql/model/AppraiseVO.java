package com.xjc.mysql.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author jiachenxu
 * @Date 2022/1/6
 * @Descripetion
 */
@Data
public class AppraiseVO implements Serializable {

    private String commentContent;

    private String replyContent;


}
