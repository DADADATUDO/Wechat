package com.winter.interceptor;

import com.winter.pojo.CurrentLoginUser;
import com.winter.utils.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{
        String url = request.getRequestURI();
        log.debug("请求路径：{}",url);

        String token = request.getHeader("Authorization");

        if(token == null ||token.trim().isEmpty()){
            response.setStatus(200);
            throw new RuntimeException("token不能为空");
        }

        if (JWTUtil.verify(token, TOKEN_SECRET)){
            CurrentLoginUser currentLogin = JWTUtil.getPayload(token, CurrentLoginUser.class);
            request.setAttribute("CURRENT_LOGIN_USER",currentLogin);
            return true;
        }else {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token验证失败\"}");
            return false;
        }
    }
}
