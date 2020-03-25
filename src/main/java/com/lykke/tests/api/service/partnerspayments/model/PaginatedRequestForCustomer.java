package com.lykke.tests.api.service.partnerspayments.model;

import lombok.Builder;
import lombok.Data;

@Data
public class PaginatedRequestForCustomer extends PaginatedRequestModel {

    private String customerId;

    @Builder(builderMethodName = "customerRequestBuilder")
    public PaginatedRequestForCustomer(int currentPage, int pageSize, String customerId) {
        super(currentPage, pageSize);
        this.customerId = customerId;
    }
}
