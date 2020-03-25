package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.service.campaigns.model.BasePaginationRequestModel;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
@QueryParameters
public class BurnRulePaginationRequest extends BasePaginationRequestModel {

    private String title;
    private String partnerId;

    @Builder(builderMethodName = "burnRulePaginationRequestBuilder")
    public BurnRulePaginationRequest(int currentPage, int pageSize, String title, String partnerId) {
        super(currentPage, pageSize);
        this.title = title;
        this.partnerId = partnerId;
    }
}
