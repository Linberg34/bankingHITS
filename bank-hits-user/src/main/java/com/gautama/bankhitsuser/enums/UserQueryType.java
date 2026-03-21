package com.gautama.bankhitsuser.enums;

import lombok.Getter;

@Getter
public enum UserQueryType {
    ALL("Все пользователи"),
    BANNED("Бан");
    private final String displayName;

    UserQueryType(String displayName) {
        this.displayName = displayName;
    }
}