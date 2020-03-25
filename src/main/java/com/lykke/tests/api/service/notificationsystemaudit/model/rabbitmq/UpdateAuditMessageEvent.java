package com.lykke.tests.api.service.notificationsystemaudit.model.rabbitmq;

import com.lykke.tests.api.service.notificationsystemaudit.model.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UpdateAuditMessageEvent {

    private String messageId;
    private String source;
    private String sentTimestamp;
    private DeliveryStatus deliveryStatus;
    private String deliveryComment;
}
