package com.winter.exception;

import com.winter.app.enums.ResponseType;
import lombok.Getter;

/**
 * 自定义业务异常类
 * 继承 RuntimeException，表示这是一个运行时异常，不需要进行try-catch处理
 * 在GlobalControllerAdvice中
 * Spring 的多态捕获： @ExceptionHandler(value = ServiceException.class) 不仅会捕获 ServiceException 本身，
 * 还会捕获所有继承自它的子类（包括 AuthException）。除非你专门写了一个针对 AuthException.class 的处理方法，否则
 * 它们都会 “掉进” 这个 ServiceException 的处理器中。
 */
@Getter
public class ServiceException extends RuntimeException {

    private final ResponseType type;
    // 删除了自定义的 private String message; 直接使用父类的 message

    public ServiceException() {
        super(ResponseType.SERVER_ERROR.getMessage()); // 默认消息
        this.type = ResponseType.SERVER_ERROR;
    }

    public ServiceException(String message) {
        super(message); // 传递给父类 RuntimeException
        this.type = ResponseType.SERVER_ERROR;
    }

    public ServiceException(ResponseType type) {
        super(type.getMessage());
        this.type = type;
    }

    public ServiceException(ResponseType type, String message) {
        super(message);
        this.type = type;
    }
}