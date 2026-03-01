package com.gautama.bankhitsuser.enums;

import lombok.Getter;

@Getter
public enum UserQueryType {
    ALL("Все пользователи"),
    CLIENTS("Только клиенты"),
    EMPLOYEES("Только сотрудники"),
    WITHOUT_ROLE("Только без роли"),
    BANNED("Бан");
    private final String displayName;

    UserQueryType(String displayName) {
        this.displayName = displayName;
    }
}