package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class EarnRulesModel {

    private String id;
    private Boolean isEnabled;
    private String createdBy;
    private String name;
    private ConditionCreateModel[] conditions;
    private String fromDate;
    private String toDate;
    private String description;
    private float reward;
    private String rewardType;
    private Integer completionCount;
    private MobileContents[] mobileContents;
}
