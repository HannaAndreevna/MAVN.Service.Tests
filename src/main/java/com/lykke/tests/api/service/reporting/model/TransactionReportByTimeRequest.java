package com.lykke.tests.api.service.reporting.model;

import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.service.campaigns.model.burnrules.BurnRuleContentCreateRequestModel;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import lombok.Builder;
import lombok.Data;

@Data
@QueryParameters
public class TransactionReportByTimeRequest extends PaginationModel {

    private String from;
    private String to;

    @Builder(builderMethodName = "transactionReportByTimeRequestBuilder")
    public TransactionReportByTimeRequest(int currentPage, int pageSize, String from, String to) {
        super(currentPage, pageSize);
        this.from = from;
        this.to = to;
    }
}
