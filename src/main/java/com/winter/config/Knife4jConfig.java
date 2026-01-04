package com.winter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j配置类
 * 用于配置Swagger2接口文档的相关信息
 * <a href="http://localhost:9090/api/doc.html">...</a>
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.winter.app.controller")) // 扫描的包路径
                .paths(PathSelectors.any()) // 定义需要生成文档的API路径
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("WeChat IM API") // 文档标题
                .description("即时通讯系统API文档") // 文档描述
                .contact(new Contact("Winter", "", "winter@example.com")) // 联系人信息
                .version("1.0") // 版本号
                .build();
    }
}