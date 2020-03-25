package com.lykke.tests.api.service.smsprovidermock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaginatedSmsRequestModel {

    private int currentPage;
    private int pageSize;
    private String phoneNumber;
    private String messageId;
}
