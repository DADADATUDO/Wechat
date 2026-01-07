package com.winter.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winter.app.mapper.ImUserMapper;
import com.winter.pojo.Response;
import com.winter.pojo.entity.ImUser;
import com.winter.utils.ReadConfigProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
@Slf4j
public class TestController {
    @Autowired
    ImUserMapper imUserMapper;
    @Resource
    private ReadConfigProperties readConfigProperties;
    
    @RequestMapping("/test")
    public Response<ImUser> test(){
        QueryWrapper<ImUser> imUserQueryWrapper = new QueryWrapper<>();
        imUserQueryWrapper.eq("user_name","winter");
        ImUser imUser = imUserMapper.selectOne(imUserQueryWrapper);
        
        if (imUser == null) {
            log.info("测试热部署test - 未找到用户名为 'winter' 的用户");
            return Response.fail("用户不存在");
        }
        
        log.info("测试热部署test - 用户信息: {}", imUser);
        //return Response.fail("用户名错误");
        return Response.success(imUser);
    }

    @RequestMapping("/testProperties")
    public Response<?> testProperties(){
        List<String> publicPath = readConfigProperties.getPublicPath();
        long loginTokenExpire = readConfigProperties.getLoginTokenExpire();
        long loginCodeExpire = readConfigProperties.getLoginCodeExpire();
        String tokenSecret = readConfigProperties.getTokenSecret();
        log.info("publicPath:{}, loginTokenExpire:{}, loginCodeExpire:{}, tokenSecret:{}", publicPath, loginTokenExpire, loginCodeExpire, tokenSecret);
        return Response.success("测试成功");
    }
}