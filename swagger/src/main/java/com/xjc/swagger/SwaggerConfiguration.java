package com.xjc.swagger;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Version 1.0
 * @ClassName SwaggerConfiguration
 * @Author jiachenXu
 * @Date 2020/11/29
 * @Description
 */
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfiguration {

    // @Api 行
    // @ApiOperation 列

    // http://ip:port/doc.html

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xjc.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo(){
        return  new ApiInfoBuilder()
                .title("SwaggerConfiguration")
                .description("SwaggerConfiguration")
                .termsOfServiceUrl("localhost:9090/")
                // "17600792030@163.com"
                .contact(new Contact("jiachenXu", "github.com/jiachen-xu95/", "17600792030@163.com"))
                .version("1.0")
                .build();
    }


}
