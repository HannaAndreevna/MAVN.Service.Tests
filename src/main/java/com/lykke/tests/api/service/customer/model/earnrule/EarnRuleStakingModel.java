package com.lykke.tests.api.service.customer.model.earnrule;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarnRuleStakingModel {

    private String referralId;
    private String referralName;
    private String stakeAmount;
    private String totalReward;
    private int stakingPeriod;
    private int stakeWarningPeriod;
    private Double stakingRule;
    private Date timestamp;
}
