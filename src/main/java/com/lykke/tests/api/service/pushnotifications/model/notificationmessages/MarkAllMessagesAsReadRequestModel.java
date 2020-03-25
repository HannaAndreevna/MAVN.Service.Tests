package com.lykke.tests.api.service.pushnotifications.model.notificationmessages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class MarkAllMessagesAsReadRequestModel {

    private String customerId;
}
