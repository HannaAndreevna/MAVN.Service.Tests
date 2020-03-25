package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetRegisteredLeadsHistoryRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private String referId;
    private String responseSalesforceId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String agentSalesforceId;
    private String responseStatus;

    @Builder(builderMethodName = "historyRegisteredLeadsBuilder")
    public GetRegisteredLeadsHistoryRequestModel(
            int pageSize,
            int currentPage,
            String fromTimestamp,
            String toTimestamp,
            String referId,
            String responseSalesforceId,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String agentSalesforceId,
            String responseStatus) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.referId = referId;
        this.responseSalesforceId = responseSalesforceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.agentSalesforceId = agentSalesforceId;
        this.responseStatus = responseStatus;
    }
}
