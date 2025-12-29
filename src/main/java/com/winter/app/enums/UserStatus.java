package com.winter.app.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    DEFAULT("DEFAULT","正常"),
    DISABLE("DISABLE","禁用");

    private final String type;
    private final String message;


    UserStatus(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
