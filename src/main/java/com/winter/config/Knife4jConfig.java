package com.winter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * 用于配置OpenAPI接口文档的相关信息
 * <a href="http://localhost:9090/api/doc.html">...</a>
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WeChat IM API")
                        .description("即时通讯系统API文档")
                        .contact(new Contact()
                                .name("Winter")
                                .email("winter@example.com"))
                        .version("1.0"));
    }
}