package com.lykke.tests.api.service.notificationsystemaudit.model.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Channel {
    EMAIL("Email"),
    SMS("Sms"),
    PUSH_NOTIFICATION("PushNotification");

    @Getter
    private String channel;
}
