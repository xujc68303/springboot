package com.xjc.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Version 1.0
 * @ClassName MysqlApplication
 * @Author jiachenXu
 * @Date 2020/10/15
 * @Description
 */
@SpringBootApplication
@MapperScan("com.xjc.mysql.mapper")
public class MysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysqlApplication.class, args);
    }
}
