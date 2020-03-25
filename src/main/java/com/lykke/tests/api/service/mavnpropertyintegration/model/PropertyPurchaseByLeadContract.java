package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class PropertyPurchaseByLeadContract {

    private float vatAmount;
    private String unitLocationCode;
    private Date timestamp;
    private String spaStatus;
    private float sellingPropertyPrice;
    private String referrerEmail;
    private String propertySalesforceId;
    private String orderNumber;
    private String opportunityId;
    private String leadEmail;
    private String mvnReferralId;
    private String accountId;
    private boolean customerExists;
    private boolean agentExists;
}
