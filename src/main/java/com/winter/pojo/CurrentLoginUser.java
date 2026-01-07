package com.winter.pojo;

import com.winter.app.enums.ClientType;
import com.winter.app.enums.Role;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
/*
  当前登录用户信息类
  用于存储当前登录用户的相关信息，将用来创建Token
 */
public class CurrentLoginUser {
    private Integer id;
    private String username;
    private String fullName;
    private Role role;
    private ClientType port;
}
