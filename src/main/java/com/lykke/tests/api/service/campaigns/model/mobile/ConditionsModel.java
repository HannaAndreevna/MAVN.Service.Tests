package com.lykke.tests.api.service.campaigns.model.mobile;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ConditionsModel {
    private String id;
    private String type;
    private String displayName;
    private long immediateReward;
    private int completionCount;
    private String[] partnerIds;
}
