package com.winter.app.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 应用登录验证码DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "应用登录验证码DTO")
public class AppCodeDto {
    // 验证码结果
    private String result;
    // 验证码生成时间戳
    private Long timestamp;
}
