package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetPropertyPurchasesByLeadsRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private String referId;
    private String salesforceId;
    private String customerBuyerEmail;

    @Builder(builderMethodName = "historyPropertyPurchasesByLeadsBuilder")
    public GetPropertyPurchasesByLeadsRequestModel(
            int pageSize,
            int currentPage,
            String fromTimestamp,
            String toTimestamp,
            String referId,
            String salesforceId,
            String customerBuyerEmail) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.referId = referId;
        this.salesforceId = salesforceId;
        this.customerBuyerEmail = customerBuyerEmail;
    }
}
