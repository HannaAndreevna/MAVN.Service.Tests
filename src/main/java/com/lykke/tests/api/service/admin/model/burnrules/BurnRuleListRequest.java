package com.lykke.tests.api.service.admin.model.burnrules;

import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.service.admin.model.PagedRequestModel;
import lombok.Builder;
import lombok.Data;

@Data
@QueryParameters
public class BurnRuleListRequest extends PagedRequestModel {

    private String title;

    @Builder(builderMethodName = "burnRuleListRequestBuilder")
    public BurnRuleListRequest(int pageSize, int currentPage, String title) {
        super(pageSize, currentPage);
        this.title = title;
    }
}
