package com.xjc.docker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @Version 1.0
 * @ClassName DockerApi
 * @Author jiachenXu
 * @Date 2020/8/23
 * @Description
 */
@Configuration
@Slf4j
public class DockerApi {

    public void request(){
        log.warn("hello docker");
    }
}
