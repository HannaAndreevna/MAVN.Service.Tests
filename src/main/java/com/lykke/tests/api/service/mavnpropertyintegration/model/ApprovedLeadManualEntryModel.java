package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ApprovedLeadManualEntryModel {

    private String mvnReferralId;
    private String salesforceId;
    private String salesmanSalesforceId;
    private LeadStatus leadStatus;
    private String leadAccountSalesforceId;
}
