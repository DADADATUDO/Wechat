package com.winter.app.enums;

import lombok.Getter;

@Getter
public enum Role {
    ROOT("ROOT", "root"),
    USER("ADMIN", "admin"),
    MEMBER("MEMBER", "member");

    private final String value;
    private final String message;

    Role(String value, String message) {
        this.value = value;
        this.message = message;
    }
}
