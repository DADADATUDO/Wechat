package com.winter.interceptor;

import com.winter.exception.AuthException;
import com.winter.pojo.CurrentLoginUser;
import com.winter.utils.JWTUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private JWTUtil jwtUtil;
    @Value("${winter.token-secret}")
    private String TOKEN_SECRET;

    @Override
    public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response,@Nullable Object handler){
        String url = request.getRequestURI();
        log.debug("请求路径：{}",url);
        /*
         *当你前端 Vue 发起 POST 请求时，浏览器会先自动发一个 OPTIONS 请求（不带 Token）询问服务器是否允许跨域。
         *后果： 你的拦截器检查 token == null，直接抛出异常。导致前端报 CORS 跨域错误，接口根本调不通。
         *修复： 必须放行 OPTIONS 请求。
         *再次强调：CorsFilter 只是加了头，拦截器这里必须放行 OPTIONS，否则预检会挂
         */
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if(token == null ||token.trim().isEmpty()){
            if (response != null) {
                response.setStatus(200);
            }
            throw new AuthException();
        }

        if (JWTUtil.verify(token, TOKEN_SECRET)){
            CurrentLoginUser currentLogin = JWTUtil.getPayload(token, CurrentLoginUser.class);
            request.setAttribute("CURRENT_LOGIN_USER",currentLogin);
            return true;
        }else {
            throw new AuthException("token校验失败");
        }
    }
}
