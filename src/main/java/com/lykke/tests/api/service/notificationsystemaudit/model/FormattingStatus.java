package com.lykke.tests.api.service.notificationsystemaudit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FormattingStatus {
    SUCCESS("Success"),
    VALUE_NOT_FOUND("ValueNotFound");

    @Getter
    private String status;
}
