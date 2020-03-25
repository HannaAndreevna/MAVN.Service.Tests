package com.lykke.tests.api.service.campaigns.model.mobile;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class EarnRulePaginatedResponseModel {
    private EarnRuleLocalizedResponse[] earnRules;
    private int currentPage;
    private int pageSize;
    private int totalCount;

    public EarnRulePaginatedResponseModel() {
        earnRules = new EarnRuleLocalizedResponse[] {};
    }
}
