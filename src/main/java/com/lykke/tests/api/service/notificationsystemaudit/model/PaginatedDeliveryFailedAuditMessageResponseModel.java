package com.lykke.tests.api.service.notificationsystemaudit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@AllArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedDeliveryFailedAuditMessageResponseModel {

    private List<DeliveryFailedAuditMessageResponseModel> auditMessages;
    private int currentPage;
    private int pageSize;
    @EqualsAndHashCode.Exclude
    private int totalCount;

    public PaginatedDeliveryFailedAuditMessageResponseModel() {
        auditMessages = new ArrayList<>();
    }
}
