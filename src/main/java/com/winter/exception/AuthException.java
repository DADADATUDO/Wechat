package com.winter.exception;

import com.winter.app.enums.ResponseType;
/**
 * 继承自ServiceException，用于处理认证相关的异常。
 */
public class AuthException extends ServiceException {
    public AuthException() {
        super(ResponseType.UNAUTHORIZED);
    }

    public AuthException(String message) {
        super(ResponseType.UNAUTHORIZED, message);
    }
}
