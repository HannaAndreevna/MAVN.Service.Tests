package com.lykke.tests.api.service.notificationsystemaudit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CallType {
    REST("Rest"),
    RABBITMQ("RabbitMq");

    @Getter
    private String type;
}
