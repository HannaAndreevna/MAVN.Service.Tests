package com.lykke.tests.api.service.operationshistory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaginationModel {

    private int currentPage;
    private int pageSize;
}
