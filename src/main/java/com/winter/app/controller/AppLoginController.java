package com.winter.app.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.tools.jconsole.JConsoleContext;
import com.wf.captcha.ArithmeticCaptcha;
import com.winter.app.enums.ClientType;
import com.winter.app.enums.ResponseType;
import com.winter.app.enums.UserStatus;
import com.winter.app.mapper.ImUserMapper;
import com.winter.app.pojo.dto.AppCodeDto;
import com.winter.app.pojo.form.AppUserLoginForm;
import com.winter.app.pojo.vo.CodeVo;
import com.winter.pojo.CurrentLoginUser;
import com.winter.pojo.Response;
import com.winter.pojo.entity.ImUser;
import com.winter.utils.AesEncryptUtil;
import com.winter.utils.BeanUtil;
import com.winter.utils.JWTUtil;
import com.winter.utils.LoginUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/app")
@Tag(name = "应用登录接口")
public class AppLoginController {

    @Resource
    private ImUserMapper imUserMapper;
    @Value("${winter.login-code-expire}")
    private int CODE_EXPIRE;
    @Value("${winter.login-code}")
    private String loginCode;
    @Value("${winter.login-code-flag}")
    private boolean loginCodeFlag;
    @Value("${winter.login-token-expire}")
    private long TOKEN_EXPIRE;
    @Value("${winter.token-secret}")
    private String TOKEN_SECRET;

    @PostMapping("/login")
    @Schema(description = "用户登录")
    public Response<?> login(@Validated @RequestBody AppUserLoginForm loginForm) {
        String password = loginForm.getPassword();
        String username = loginForm.getUsername();
        String code = loginForm.getCode();
        String key = loginForm.getKey();

        //万能验证码校验
        boolean bypassCaptcha = loginCodeFlag && loginCode != null && !loginCode.isEmpty() && loginCode.equals(code);
        //如果没有使用万能验证码，需要校验验证码
        if (!bypassCaptcha) {
            if (key == null || key.isEmpty()) {
                return Response.fail("验证码key不能为空",key);
            }
            try {
                //解密验证码key，获取验证码结果和时间戳，和用户输入的code验证码作比较。
                String result = AesEncryptUtil.decrypt(key);
                AppCodeDto appCodeDto = JSON.parseObject(result, AppCodeDto.class);
                if (appCodeDto == null || appCodeDto.getTimestamp() == null || appCodeDto.getResult() == null) {
                    return Response.fail("验证码key解密后的结果为空");
                }

                String codeResult = appCodeDto.getResult();
                Long timestamp = appCodeDto.getTimestamp();
                Long timeNow = System.currentTimeMillis();
                if (timeNow - timestamp > CODE_EXPIRE) {
                    return Response.fail("验证码过期");
                }
                if (!code.equals(codeResult)) {
                    return Response.fail("验证码错误");
                }
            } catch (Exception e) {
                return Response.fail("验证码key非法");
            }
        }

        QueryWrapper<ImUser> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("user_name",username).select("id","user_name","password","full_name","salt").last("limit 1");
        ImUser imUser = imUserMapper.selectOne(queryWrapper);
        if (imUser == null) {
            return Response.fail("账号秘密错误");
        }
        if (!LoginUtil.formPwdEqDbPWd(password,imUser.getPassword(),imUser.getSalt())){
            return Response.fail("账号秘密错误");
        }
        if (UserStatus.DISABLE.equals(imUser.getStatus())){
            return Response.fail(ResponseType.USER_DISABLED);
        }

        //逐个复制，如果属性很多就比较麻烦。使用下面的BeanUtils来复制
        //CurrentLoginUser currentLoginUser = new CurrentLoginUser();
        //currentLoginUser.setId(imUser.getId()).setUsername(imUser.getUserName()).setName(imUser.getFullName());

        //将当前分必要的用户信息复制到CurrentLoginUser对象中，使用JWT创建Token
        CurrentLoginUser currentLoginUser = BeanUtil.copyProperties(imUser, CurrentLoginUser.class);
        currentLoginUser.setPort(ClientType.APP);
        log.info("currentLoginUser:{}",currentLoginUser);

        // 使用JWTUtil创建Token，将CurrentLoginUser对象作为负载信息，设置过期时间和密钥。同样的信息也能从Token中解码出来。
        String token = JWTUtil.sign(currentLoginUser, TOKEN_EXPIRE, TOKEN_SECRET);
        return Response.success(token);
    }



    /**
     * 获取验证码接口
     * 生成一个算术验证码，并将验证码结果加密后返回给前端
     * 
     * @return Response<CodeVo> 包含验证码图片(base64格式)和加密密钥的响应对象。加过密的key中是验证码结果和时间戳的JSON字符串
     * @throws Exception 加密过程中可能抛出的异常
     */
    @GetMapping("/code")
    @Schema(description = "获取验证码")
    public Response<CodeVo> getCode() throws Exception {
        // 创建一个算术验证码对象，设置图片宽度为130像素，高度为45像素
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 45);
        // 生成算术表达式字符串（例如："1+2="）
        captcha.getArithmeticString();
        // 获取验证码的正确答案（例如："3"）
        String result = captcha.text();

        // 创建登录验证码DTO对象，用于存储验证码信息
        AppCodeDto codeDto = new AppCodeDto()
                // 设置验证码的正确答案
                .setResult(result)
                // 设置当前时间戳，用于验证码有效期验证
                .setTimestamp(System.currentTimeMillis());

        // 将验证码DTO对象转换为JSON字符串
        String content = JSON.toJSONString(codeDto);
        // 使用AES加密算法对JSON字符串进行加密，得到加密后的密钥
        String encryptKey = AesEncryptUtil.encrypt(content);

        // 创建验证码视图对象，用于返回给前端
        CodeVo codeVo = new CodeVo()
                // 将验证码图片转换为Base64编码字符串
                .setImg(captcha.toBase64())
                // 设置加密后的密钥
                .setKey(encryptKey);
        
        // 返回成功响应，包含验证码图片和加密密钥
        return Response.success(codeVo);
    }
}
