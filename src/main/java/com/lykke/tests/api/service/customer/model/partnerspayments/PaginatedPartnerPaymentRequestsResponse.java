package com.lykke.tests.api.service.customer.model.partnerspayments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedPartnerPaymentRequestsResponse {

    private int currentPage;
    private int pageSize;
    private int totalCount;
    private PartnerPaymentRequestItemResponse[] paymentRequests;
}
