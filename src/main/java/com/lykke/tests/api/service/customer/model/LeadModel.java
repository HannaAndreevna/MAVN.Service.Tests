package com.lykke.tests.api.service.customer.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.customer.model.referral.ReferralLeadStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class LeadModel {

    private String name;
    private ReferralLeadStatus status;
    private String timeStamp;
    private int purchaseCount;
    private int offersCount;
}
