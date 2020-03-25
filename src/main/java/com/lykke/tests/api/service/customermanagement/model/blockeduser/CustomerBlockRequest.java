package com.lykke.tests.api.service.customermanagement.model.blockeduser;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(UpperCamelCaseStrategy.class)
public class CustomerBlockRequest {
    private String customerId;

}
