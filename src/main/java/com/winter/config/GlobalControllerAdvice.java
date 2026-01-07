package com.winter.config;

import com.winter.app.enums.ResponseType;
import com.winter.exception.ServiceException;
import com.winter.pojo.Response;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@ControllerAdvice
/*
 GlobalControllerAdvice全局异常处理类，用于捕获所有异常并返回统一格式的错误响应。
 同时实现ResponseBodyAdvice接口，确保所有响应都包装成统一格式。

 作用时机：在控制器方法执行之后，响应体写入之前
 适用场景：统一处理响应体、修改响应内容、添加公共字段等
 优势：可以直接操作响应对象，更灵活、更安全

 http请求 → 拦截器(preHandle，AppConfiguration中) → controller方法 →
 → 异常处理器(GlobalControllerAdvice中) → ResponseBodyAdvice(GlobalControllerAdvice实现ResponseBodyAdvice)
 → 响应
 */
public class GlobalControllerAdvice implements ResponseBodyAdvice<Object> {

    //==========GlobalControllerAdvice的功能来实现异常的拦截===========
    /**
     * 处理服务异常（ServiceException），返回自定义的错误响应。
     * 此项能捕获所有继承自ServiceException的异常，包括自定义的异常类。
     * @param e 服务异常对象
     * @return 包含错误类型和错误信息的响应对象
     */
    @ResponseBody
    @ExceptionHandler(value = ServiceException.class)
    public Response<String> serviceErrorHandler(ServiceException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        ResponseType responseType = e.getType();
        return Response.fail(responseType, e.getMessage());
    }

    /**
     * 处理其他未捕获的异常，返回系统错误响应，兜底所有错误
     * @param e 异常对象
     * @return 包含错误类型和错误信息的响应对象
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response<String> globalErrorHandler(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Response.fail(ResponseType.SERVER_ERROR, "系统异常，请联系管理员");
    }


    //==========ResponseBodyAdvice的功能来实现返回体的拦截且统一格式化===========
    /**
     * 判断是否需要处理响应体
     * @param returnType 方法参数
     * @param converterType 转换器类型
     * @return true表示需要处理，false表示跳过
     */
    @Override
    public boolean supports(MethodParameter returnType,@Nullable Class converterType) {
        // 如果返回类型已经是Response，则不需要再次包装
        return !returnType.getParameterType().equals(Response.class);
    }

    /**
     * 在响应体写入之前进行处理，将非Response类型的返回值包装成Response
     * @param body 原始返回值
     * @param returnType 方法参数
     * @param selectedContentType 选择的内容类型
     * @param selectedConverterType 选择的转换器类型
     * @param request 请求对象
     * @param response 响应对象
     * @return 包装后的Response对象
     */
    @Override
    public Object beforeBodyWrite(Object body, @Nullable MethodParameter returnType, @Nullable MediaType selectedContentType,
                                  @Nullable Class selectedConverterType, @Nullable ServerHttpRequest request,
                                  @Nullable ServerHttpResponse response) {
        // 如果返回值已经是Response类型，直接返回
        if (body instanceof Response) {
            return body;
        }
        // 如果返回值为null，返回空的成功响应
        if (body == null) {
            return Response.success();
        }
        // 其他情况，将返回值包装成成功响应
        return Response.success(body);
    }
}
