package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetProcessedAgentsHistoryRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private ProcessedAgentStatus agentStatus;
    private String salesforceId;
    private String salesmanSalesforceId;
    private String referId;

    @Builder(builderMethodName = "processedAgentsHistoryRequestBuilder")
    public GetProcessedAgentsHistoryRequestModel(
            int pageSize,
            int currentPage,
            String fromTimestamp,
            String toTimestamp,
            ProcessedAgentStatus agentStatus,
            String salesforceId,
            String salesmanSalesforceId,
            String referId) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.agentStatus = agentStatus;
        this.salesforceId = salesforceId;
        this.salesmanSalesforceId = salesmanSalesforceId;
        this.referId = referId;
    }
}
