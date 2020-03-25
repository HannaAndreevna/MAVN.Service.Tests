package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ProcessedAgentManualEntryModel {

    private String timeStamp;
    private String salesforceId;
    private String salesmanSalesforceId;
    private ProcessedAgentStatus agentStatus;
    private String mvnReferralId;

    public String getAgentStatus() {
        return agentStatus.getCode();
    }
}
