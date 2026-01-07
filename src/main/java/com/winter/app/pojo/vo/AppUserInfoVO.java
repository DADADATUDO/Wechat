package com.winter.app.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.winter.app.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Schema(description = "当用户实体可给前端的信息")
public class AppUserInfoVO {
    @TableId(value = "id",type = IdType.AUTO)
    @Schema(description = "用户ID，自增主键")
    private Integer id;

    @Schema(description = "账号，登录时使用")
    @TableField(value = "user_name")
    private String username;

    @Schema(description = "昵称，前端显示的名称")
    @TableField(value = "nickname")
    private String nickname;

    @Schema(description = "全名")
    @TableField(value = "full_name")
    private String fullName;

    @Schema(description = "性别")
    @TableField(value = "sex")
    private Integer sex;

    @Schema(description = "省份")
    @TableField(value = "province")
    private String province;

    @Schema(description = "城市")
    @TableField(value = "city")
    private String city;

    @Schema(description = "国家")
    @TableField(value = "country")
    private String country;

    @Schema(description = "头像")
    @TableField(value = "avatar")
    private String avatar;

//    @Schema(description = "手机号")
//    @TableField(value = "phone_number")
//    private String phoneNumber;
//
//    @Schema(description = "身份证号")
//    @TableField(value = "id_card")
//    private String idCard;

    @Schema(description = "状态")
    @TableField(value = "status")
    private UserStatus status;

//    @Schema(description = "地址")
//    @TableField(value = "address")
//    private String address;

    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
}
