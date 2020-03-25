package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
@NetClassName("PaginatedBurnRuleListResponse")
public class PaginatedBurnRuleListResponseModel extends BasePaginationResponseModel {

    private BurnRuleInfoResponseModel[] burnRules;

    public PaginatedBurnRuleListResponseModel() {
        burnRules = new BurnRuleInfoResponseModel[]{};
    }

    @Builder(builderMethodName = "paginatedBurnRuleListResponseModelBuilder")
    public PaginatedBurnRuleListResponseModel(int currentPage, int pageSize, int totalCount,
            BurnRuleInfoResponseModel[] burnRules) {
        super(currentPage, pageSize, totalCount);
        this.burnRules = burnRules;
    }
}
