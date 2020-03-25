package com.lykke.tests.api.service.customer.model.partnerspayments;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class PaginatedRequestModel {

    private int currentPage;
    private int pageSize;
}
