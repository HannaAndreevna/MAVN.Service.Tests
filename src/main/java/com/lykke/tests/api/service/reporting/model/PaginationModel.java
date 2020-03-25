package com.lykke.tests.api.service.reporting.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class PaginationModel {

    private int currentPage;
    private int pageSize;
}
