package com.winter.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.winter.app.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 * 对应数据库表：Im_user
 *  * 业务说明：存储系统用户的基础信息（账号、昵称、密码等）
 */
@Data
@TableName("Im_user")
public class ImUser {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer Id;

    private String userName;
    private String password;
    private String salt;
    private String fullName;
    private Integer sex;
    private String province;
    private String city;
    private String country;
    private String avatar;
    private String phoneNumber;
    private String idCard;
    private UserStatus status;
    private String address;
    private LocalDateTime createTime;
}
