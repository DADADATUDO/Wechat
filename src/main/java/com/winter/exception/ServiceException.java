package com.winter.exception;

import com.winter.app.enums.ResponseType;
import lombok.Getter;

/**
 * 继承自RuntimeException，作为App的基础异常类。用于处理服务相关的异常。
 * 会根据子类的异常类型返回不同的错误信息。
 */
@Getter
public class ServiceException extends RuntimeException {
    private final ResponseType type;
    private String message = null;

    public ServiceException() {
        super();
        this.type = ResponseType.SERVER_ERROR;
    }

    public ServiceException(String message) {
        super(message);
        this.type = ResponseType.SERVER_ERROR;
        this.message = message;
    }

    public ServiceException(ResponseType type) {
        super();
        this.type = type;
    }

    public ServiceException(ResponseType type, String message) {
        super(message);
        this.type = type;
        this.message = message;
    }
}
