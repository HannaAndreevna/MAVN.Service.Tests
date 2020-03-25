package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class PropertyPurchaseByLeadManualEntryModel {

    private float vatAmount;
    private String unitLocationCode;
    private String timestamp;
    private String spaStatus;
    private float sellingPropertyPrice;
    private String referrerEmail;
    private String propertySalesforceId;
    private String orderNumber;
    private String opportunityId;
    private float netPropertyPrice;
    private String leadEmail;
    private String mvnReferralId;
    private float discountAmount;
    private String accountNumber;
    private String accountId;
}
