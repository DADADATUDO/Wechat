package com.winter.interceptor;

import com.alibaba.fastjson.JSON;
import com.winter.pojo.CurrentLoginUser;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String REQUEST_PARAMS_ATTRIBUTE = "requestParams";

    /**
     * 预处理请求，记录请求路径和参数。
     * @param request HttpServletRequest对象，包含请求信息
     * @param response HttpServletResponse对象，包含响应信息
     * @param handler 处理请求的对象，通常是Controller方法
     * @return true表示继续处理，false表示中断处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response,@Nullable Object handler){
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        // 记录请求信息
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getRemoteAddr(request);

        // 获取当前登录用户信息
        CurrentLoginUser currentLoginUser =(CurrentLoginUser) request.getAttribute("CURRENT_LOGIN_USER");
        String userInfo=currentLoginUser != null?
                String.format("用户[id=%s , username=%s]", currentLoginUser.getId(), currentLoginUser.getUsername())
                :"未登录";

        // 获取request中的请求参数
        Map<String, Object> params = new HashMap<>();
        // Enumeration是枚举类型，用于迭代集合中的元素
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                params.put(paramName, paramValues[0]);
            } else {
                params.put(paramName, paramValues);
            }
        }
        // 保存请求参数，以便在afterCompletion中使用
        request.setAttribute(REQUEST_PARAMS_ATTRIBUTE, params);

        // 记录请求日志
        log.info("请求开始 - URI: {}, 方法: {}, IP: {}, 用户: {}, 参数: {}",
                requestURI, method, remoteAddr, userInfo, JSON.toJSONString(params));

        return true;
    }


    /**
     * 请求处理完成，记录请求完成时间、响应状态码和响应时间。
     * @param request HttpServletRequest对象，包含请求信息
     * @param response HttpServletResponse对象，包含响应信息
     * @param handler 处理请求的对象，通常是Controller方法
     * @param ex 异常对象，如果请求处理过程中发生异常，则记录异常信息
     */
    @Override
    public void afterCompletion(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler, Exception ex){
        // 记录请求耗时
        long attribute =(Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - attribute;

        // 获取请求信息
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int status = 0;
        if (response != null) {
            status = response.getStatus();
        }

        // 获取请求参数
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.getAttribute(REQUEST_PARAMS_ATTRIBUTE);

        // 获取当前登录用户信息
        CurrentLoginUser currentUser = (CurrentLoginUser) request.getAttribute("CURRENT_LOGIN_USER");
        String userInfo = currentUser != null ?
                String.format("用户[id=%s, username=%s]", currentUser.getId(), currentUser.getUsername()) :
                "未登录用户";

        // 记录响应日志
        if (ex != null) {
            log.error("请求异常 - URI: {}, 方法: {}, 状态: {}, 耗时: {}ms, 用户: {}, 参数: {}, 异常: {}",
                    requestURI, method, status, executionTime, userInfo, JSON.toJSONString(params), ex.getMessage(), ex);
        } else {
            log.info("请求完成 - URI: {}, 方法: {}, 状态: {}, 耗时: {}ms, 用户: {}, 参数: {}",
                    requestURI, method, status, executionTime, userInfo, JSON.toJSONString(params));
        }

    }


    /**
     * 获取客户端真实IP地址
     */
    private String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
