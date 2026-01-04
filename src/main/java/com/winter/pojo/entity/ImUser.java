package com.winter.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.winter.app.enums.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户表
 * 对应数据库表：Im_user
 *  * 业务说明：存储系统用户的基础信息（账号、昵称、密码等）
 */
@Data
@TableName("im_user")
@Accessors(chain = true)
public class ImUser {
    @TableId(value = "id",type = IdType.AUTO)
    @ApiModelProperty(value = "用户ID，自增主键")
    private Integer id;

    @ApiModelProperty(value = "账号，登录时使用")
    @TableField(value = "user_name")
    private String userName;

    @ToString.Exclude
    @ApiModelProperty(value = "密码")
    @TableField(value = "password")
    private String password;

    @ApiModelProperty(value = "盐值")
    @TableField(value = "salt")
    private String salt;

    @ApiModelProperty(value = "昵称，前端显示的名称")
    @TableField(value = "nickname")
    private String nickname;

    @ApiModelProperty(value = "全名")
    @TableField(value = "full_name")
    private String fullName;

    @ApiModelProperty(value = "性别")
    @TableField(value = "sex")
    private Integer sex;

    @ApiModelProperty(value = "省份")
    @TableField(value = "province")
    private String province;

    @ApiModelProperty(value = "城市")
    @TableField(value = "city")
    private String city;

    @ApiModelProperty(value = "国家")
    @TableField(value = "country")
    private String country;

    @ApiModelProperty(value = "头像")
    @TableField(value = "avatar")
    private String avatar;

    @ApiModelProperty(value = "手机号")
    @TableField(value = "phone_number")
    private String phoneNumber;

    @ApiModelProperty(value = "身份证号")
    @TableField(value = "id_card")
    private String idCard;

    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    private UserStatus status;

    @ApiModelProperty(value = "地址")
    @TableField(value = "address")
    private String address;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
}