package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetApprovedLeadsHistoryRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private String referId;
    private String salesforceId;
    private String salesmanSalesforceId;

    @Builder(builderMethodName = "historyApprovedLeadsBuilder")
    public GetApprovedLeadsHistoryRequestModel(int pageSize, int currentPage, String fromTimestamp, String toTimestamp,
            String referId, String salesforceId, String salesmanSalesforceId) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.referId = referId;
        this.salesforceId = salesforceId;
        this.salesmanSalesforceId = salesmanSalesforceId;
    }
}
