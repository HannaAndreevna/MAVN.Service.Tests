package com.lykke.tests.api.service.customer.model.earnrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarnRuleBaseModel {

    private String id;
    private String title;
    private CampaignStatus status;
    private String description;
    private String imageUrl;
    private String reward;
    private RewardType rewardType;
    private Date fromDate;
    private Date toDate;
    private String createdBy;
    private Date creationDate;
    private int completionCount;
    private String approximateAward;
    private boolean isApproximate;
    private int order;
}
