package com.lykke.tests.api.service.notificationsystemaudit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PaginatedAuditMessageRequestModel {

    private int currentPage;
    private int pageSize;
    private String fromCreationTimestamp;
    private String toCreationTimestamp;
    private String messageType;
    private String customerId;
    private String deliveryStatus;
    private String source;
    private String messageGroupId;
    private String messageId;
}
