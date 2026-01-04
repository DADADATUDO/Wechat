package com.winter.app.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "用户注册表单")
public class AppUserRegistryForm {

    @NotEmpty(message = "账号不能为空")
    @ApiModelProperty(value = "账号",required = true)
    @Size(min = 6,max = 16,message = "账号长度为6-16个字符")
    private String username;

    @Size(min = 6,max = 16,message = "密码长度为6-16个字符")
    @Pattern(regexp = "^[0-9a-zA-Z~!@#$%^&*()_+\\-=]+$", message = "密码必须是数字、字母、特殊字符~!@#$%^&*()_+-=.的组合")
    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码",required = true)
    private String password;

    @Size(max = 20,message = "昵称长度不能超过20个字符")
    @NotEmpty(message = "昵称不能为空")
    @ApiModelProperty(value = "昵称",required = true)
    private String nickname;

    /**
     * 对用户名进行trim操作，移除首尾空格
     * 如果用户名为空，则设置为null
     *
     * @param username 原始用户名
     */
    public void setUsername(String username) {
        if (username != null) {
            this.username = username.trim();
        } else {
            this.username = null;
        }
    }

    /**
     * 对昵称进行trim操作，移除首尾空格
     * 如果昵称为空，则设置为null
     *
     * @param nickname 原始昵称
     */
    public void setNickname(String nickname) {
        if (nickname != null) {
            this.nickname = nickname.trim();
        } else {
            this.nickname = null;
        }
    }
}


