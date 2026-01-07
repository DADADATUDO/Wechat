package com.winter.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "winter")
public class ReadConfigProperties {

    private String tokenSecret;

    private long loginTokenExpire;

    private long loginCodeExpire;

    //因为使用@Value("${winter.public-path}")是无法读取到yml中的数组，所以这里使用@ConfigurationProperties(prefix = "winter")来读取
    private List<String> publicPath;
}
