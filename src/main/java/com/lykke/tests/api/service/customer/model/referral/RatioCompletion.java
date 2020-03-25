package com.lykke.tests.api.service.customer.model.referral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatioCompletion {

    private String paymentId;
    private String name;
    private Double givenThreshold;
    private int checkpoint;
    private String givenRatioRewardBonus;
    private String totalRatioRewardBonus;
}
