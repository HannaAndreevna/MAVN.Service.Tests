package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.Builder;
import lombok.Data;

@Data
public class GetRegisteredAgentsHistoryRequestModel extends PaginatedRequestModel {

    private String fromTimestamp;
    private String toTimestamp;
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneCodeAndNumber;
    private String responseAgentSalesforceId;
    private String responseStatus;
    private AgentRegisterStatus responseAgentStatus;

    @Builder(builderMethodName = "registeredAgentsHistoryRequest")
    public GetRegisteredAgentsHistoryRequestModel(
            int pageSize,
            int currentPage,
            String fromTimestamp,
            String toTimestamp,
            String customerId,
            String firstName,
            String lastName,
            String email,
            String phoneCodeAndNumber,
            String responseAgentSalesforceId,
            String responseStatus) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneCodeAndNumber = phoneCodeAndNumber;
        this.responseAgentSalesforceId = responseAgentSalesforceId;
        this.responseStatus = responseStatus;
    }
}
