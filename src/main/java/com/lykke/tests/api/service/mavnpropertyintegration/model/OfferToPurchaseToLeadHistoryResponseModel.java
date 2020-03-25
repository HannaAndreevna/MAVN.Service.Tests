package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferToPurchaseToLeadHistoryResponseModel {

    private float vatAmount;
    private String unitLocationCode;
    private Date timestamp;
    private float sellingPropertyPrice;
    private String referrerEmail;
    private String propertySalesforceId;
    private String orderNumber;
    private String opportunityId;
    private float netPropertyPrice;
    private String leadEmail;
    private String referId;
    private float discountAmount;
    private String accountNumber;
    private String accountId;
}
