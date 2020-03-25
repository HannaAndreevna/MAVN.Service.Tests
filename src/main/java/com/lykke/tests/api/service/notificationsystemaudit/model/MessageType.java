package com.lykke.tests.api.service.notificationsystemaudit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageType {
    EMAIL("Email"),
    SMS("Sms"),
    PUSH_NOTIFICATION("PushNotification");

    @Getter
    private String type;
}
