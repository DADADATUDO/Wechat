package com.winter.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winter.app.enums.UserStatus;
import com.winter.app.mapper.ImUserMapper;
import com.winter.app.pojo.form.AppUserRegistryForm;
import com.winter.pojo.Response;
import com.winter.pojo.entity.ImUser;
import com.winter.utils.LoginUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/app/user")
@Api(tags = "用户接口")
public class AppUserController {
    @Resource
    private ImUserMapper imUserMapper;


    @PostMapping("/registry")
    @ApiOperation(value = "用户注册")
    public Response<?> registry( @Validated @RequestBody AppUserRegistryForm form){
        String username = form.getUsername();
        QueryWrapper<ImUser> imUserQueryWrapper = new QueryWrapper<>();
        imUserQueryWrapper.eq("user_name", username).select("id").last("limit 1");
        ImUser user = imUserMapper.selectOne(imUserQueryWrapper);
        if (user!=null){
            return Response.fail("账号已存在");
        }
        String salt = RandomStringUtils.randomAlphabetic(6);
        String dbPassword = LoginUtil.formPwdToDbPwd(form.getPassword(), salt);
        ImUser imUser = new ImUser()
                .setUserName(username)
                .setPassword(dbPassword)
                .setSalt(salt)
                .setFullName(form.getNickname())
                .setSex(0)
                .setStatus(UserStatus.DEFAULT)
                .setCreateTime(LocalDateTime.now());
        imUserMapper.insert(imUser);
        return Response.success("注册成功");
    }
}