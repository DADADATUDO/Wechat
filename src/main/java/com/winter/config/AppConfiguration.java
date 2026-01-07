package com.winter.config;

import com.winter.interceptor.AuthInterceptor;
import com.winter.interceptor.LoggingInterceptor;
import com.winter.utils.ReadConfigProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * SpringMVC配置类：注册拦截器
 * 继承WebMvcConfigurationSupport后，会接管SpringMVC的默认配置（新手注意：如果需要静态资源放行，要额外配置）
 * 拦截器 (HandlerInterceptor)
 * 作用时机：在controller方法执行之前和之后
 * 适用场景：请求预处理、权限检查、日志记录等
 * 限制：无法直接修改响应体的内容，只能获取到 HttpServletResponse 对象进行底层操作
 *时机问题：拦截器的 postHandle 方法在控制器方法执行后、视图渲染前执行，但此时响应体可能已经写入
 */
@Configuration
public class AppConfiguration implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;
    @Resource
    private ReadConfigProperties readConfigProperties;
    @Resource
    private LoggingInterceptor loggingInterceptor;

    public void addInterceptors(InterceptorRegistry registration){
        //1. 创建拦截器实例
        //别new，通过注入交给spring管理
        //AuthInterceptor authInterceptor = new AuthInterceptor();

        List<String> publicPath = readConfigProperties.getPublicPath();

        //注册拦截器
        // 1. 先注册日志拦截器（后执行）
        registration.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(2); // 设置优先级，数字越小优先级越高

        // 2. 后注册认证拦截器（先执行）
        registration.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(publicPath)
                .order(1); // 设置优先级，数字越小优先级越高
    }
}
