package com.lykke.tests.api.service.partnerspayments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaginatedRequestModel {

    private int currentPage;
    private int pageSize;
}
