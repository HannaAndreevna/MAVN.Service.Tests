package com.lykke.tests.api.service.bonusengine.model;

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
public class CampaignCompletionModel {

    private String id;
    private String customerId;
    private int campaignCompletionCount;
    private String campaignId;
    private boolean isCompleted;
    private ConditionCompletionModel[] conditionCompletions;
}
