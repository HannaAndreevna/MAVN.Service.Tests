package com.lykke.tests.api.service.mavnpropertyintegration.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class OfferToPurchaseToLeadsManualEntryModel {

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
    private String mvnReferralId;
    private float discountAmount;
    private String accountNumber;
    private String accountId;
}
