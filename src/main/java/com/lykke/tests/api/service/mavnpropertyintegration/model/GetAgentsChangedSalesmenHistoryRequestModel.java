package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetAgentsChangedSalesmenHistoryRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private String agentSalesforceId;

    @Builder(builderMethodName = "historyAgentsChangedSalesmenBuilder")
    public GetAgentsChangedSalesmenHistoryRequestModel(int pageSize, int currentPage, String fromTimestamp,
            String toTimestamp, String agentSalesforceId) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.agentSalesforceId = agentSalesforceId;
    }
}
