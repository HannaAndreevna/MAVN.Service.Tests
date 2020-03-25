package com.lykke.tests.api.service.customerprofile.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class CustomerProfileQueryParams {

    private int pageSize;
    private int currentPage;
}
