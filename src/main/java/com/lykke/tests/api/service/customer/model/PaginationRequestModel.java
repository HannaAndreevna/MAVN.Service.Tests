package com.lykke.tests.api.service.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaginationRequestModel {

    private int currentPage;
    private int pageSize;
}
