package com.lykke.tests.api.service.bonuscustomerprofile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CustomerResponseModel extends BonusEngineErrorResponseModel {

    private String customerId;
    private int totalCampaignsContributedCount;
    private int totalReferredFriendCount;
    private float totalPurchasedAmount;
    private int totalReferredPurchaseCount;
    private float totalReferredPurchasedAmount;
    private int totalReferredEstateLeadsCount;
    private int totalReferredEstatePurchasesCount;
    private float totalReferredEstatePurchasesAmount;
    private int totalPropertyPurchasesByLeadCount;
    private float totalPropertyPurchasesByLeadAmount;
    private int totalOfferToPurchaseByLeadCount;
    private int totalHotelStayCount;
    private float totalHotelStayAmount;
    private int totalHotelReferralStayCount;
    private float totalHotelReferralStayAmount;
}
