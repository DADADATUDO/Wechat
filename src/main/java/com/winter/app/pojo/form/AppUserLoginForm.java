package com.winter.app.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "用户登录表单")
@Accessors(chain = true)
public class AppUserLoginForm {
    @Schema(description = "账号")
    @NotEmpty(message = "账号不能为空")
    private String username;

    @Schema(description = "密码")
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "用户输入的验证码")
    @NotEmpty(message = "验证码不能为空")
    private String code;

    @Schema(description = "验证码key,登录时携带该key，用语校验验证码key{result,timeStamp}")
    private String key;
}
