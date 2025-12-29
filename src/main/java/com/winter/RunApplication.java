package com.winter;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.winter.app.mapper")
@Slf4j
public class RunApplication {
    public static void main(String[] args){
        SpringApplication.run(RunApplication.class, args);
        log.info("----------------RunApplication start----------------");
    }
}
