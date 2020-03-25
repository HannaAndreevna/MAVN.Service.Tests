package com.lykke.tests.api.service.admin.model.earnrules;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@QueryParameters
public class EarnRuleListRequest extends PagedRequestModel {

    private String earnRuleName;

    @Builder(builderMethodName = "campaignBuilder")
    public EarnRuleListRequest(int pageSize, int currentPage, String earnRuleName) {
        super(pageSize, currentPage);
        this.earnRuleName = earnRuleName;
    }
}
