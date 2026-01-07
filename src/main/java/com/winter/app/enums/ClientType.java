package com.winter.app.enums;

import lombok.Getter;

@Getter
public enum ClientType {

    APP("APP", "应用端"),
    WEB("WEB", "控制台");

    private final String value;
    private final String message;

    ClientType(String value, String message) {
        this.value = value;
        this.message = message;
    }
}
