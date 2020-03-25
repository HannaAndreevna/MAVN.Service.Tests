package com.lykke.tests.api.common.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class ByCustomerIdRequestModel {

    private String customerId;
}
