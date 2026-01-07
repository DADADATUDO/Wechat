package com.winter.app.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "验证码VO")
public class CodeVo {

    @Schema(description = "验证码图片Base64编码")
    private String img;

    @Schema(description = "验证码key,后续登录请求需要携带该key")
    private String key;
}
