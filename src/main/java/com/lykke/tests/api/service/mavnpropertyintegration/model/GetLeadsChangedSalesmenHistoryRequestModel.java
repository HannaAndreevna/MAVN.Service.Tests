package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetLeadsChangedSalesmenHistoryRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private String leadSalesforceId;

    @Builder(builderMethodName = "historyLeadsChangedSalesmenBuilder")
    public GetLeadsChangedSalesmenHistoryRequestModel(int pageSize, int currentPage, String fromTimestamp,
            String toTimestamp, String leadSalesforceId) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.leadSalesforceId = leadSalesforceId;
    }
}
