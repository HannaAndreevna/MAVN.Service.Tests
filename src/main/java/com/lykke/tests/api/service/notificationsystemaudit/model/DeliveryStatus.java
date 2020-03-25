package com.lykke.tests.api.service.notificationsystemaudit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeliveryStatus {
    SUCCESS("Success"),
    FAILED("Failed"),
    PENDING("Pending");

    @Getter
    private String status;
}
