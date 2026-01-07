package com.winter.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winter.app.enums.ResponseType;
import com.winter.exception.ServiceException;
import com.winter.pojo.Response;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper; // 用于手动序列化 JSON

    // ================== 异常处理区域 ==================

    /**
     * 1. 业务异常处理 (ServiceException)
     * 说明：开发人员主动抛出的已知异常
     */
    @ResponseBody
    @ExceptionHandler(value = ServiceException.class)
    public Response<String> serviceErrorHandler(ServiceException e) {
        // 业务异常通常不需要打印堆栈信息，只打印简单的 message 即可，避免日志刷屏
        // 如果需要调试，可以将 e 传入 log.warn 的第二个参数
        log.warn("业务异常: type={}, message={}", e.getType().getMessage(), e.getMessage());
        return Response.fail(e.getType(), e.getMessage());
    }

    /**
     * 2. 参数校验异常处理 (Spring Validator)
     * 说明：处理 @RequestBody 上 @Valid/@Validated 校验失败的情况
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 拼接所有错误信息，例如："用户名不能为空; 密码长度不够"
        String msg = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return Response.fail(ResponseType.PARAMS_ERROR, msg);
    }

    /**
     * 3. 参数绑定异常 (Get请求参数)
     * 说明：处理 Get 请求中对象参数校验失败的情况
     */
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public Response<String> handleBindException(BindException e) {
        String msg = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", msg);
        return Response.fail(ResponseType.PARAMS_ERROR, msg);
    }

    /**
     * 4. 这里的参数校验异常 (单参数校验)
     * 说明：处理 @RequestParam 上 @Validated 校验失败的情况
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<String> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getMessage();
        log.warn("参数校验失败: {}", msg);
        return Response.fail(ResponseType.PARAMS_ERROR, msg);
    }

    /**
     * 5. 全局兜底异常
     * 说明：所有未知的、意料之外的错误
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response<String> globalErrorHandler(Exception e) {
        // 系统级异常，必须打印堆栈信息，方便排查 bug
        log.error("系统严重异常: {}", e.getMessage(), e);
        return Response.fail(ResponseType.SERVER_ERROR, "系统繁忙，请稍后重试");
    }

    // ================== 统一响应封装区域 ==================
    @Override
    public boolean supports(MethodParameter returnType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果接口返回的类型本身就是 Response，或者被标注了 @IgnoreResponseAdvice (如果你有定义的话)，则不处理
        // 这里暂时只判断是否已经是 Response
        return !returnType.getParameterType().equals(Response.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, @Nullable MethodParameter returnType,@Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response) {

        // 1. 如果是 String 类型，必须手动序列化！这是最容易踩的坑
        if (body instanceof String) {
            try {
                // 设置响应头为 JSON，否则浏览器可能当做纯文本解析
                if (response != null) {
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                }
                return objectMapper.writeValueAsString(Response.success(body));
            } catch (JsonProcessingException e) {
                log.error("String类型响应序列化失败", e);
                return Response.fail(ResponseType.SERVER_ERROR, "内部序列化错误");
            }
        }

        // 2. 如果已经是 Response 类型，直接返回
        if (body instanceof Response) {
            return body;
        }

        // 3. 处理 null 值
        if (body == null) {
            return Response.success();
        }

        // 4. 其他普通对象，包装成 Response
        return Response.success(body);
    }
}