package com.lykke.tests.api.service.admin.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BurnRulesListResponse {

    private PagedResponseModel pagedResponse;
    private BurnRuleInfoModel[] burnRules;

    public BurnRulesListResponse() {
        burnRules = new BurnRuleInfoModel[]{};
    }
}
