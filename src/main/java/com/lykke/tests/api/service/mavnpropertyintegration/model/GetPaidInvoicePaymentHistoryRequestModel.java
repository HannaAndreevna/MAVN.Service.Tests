package com.lykke.tests.api.service.mavnpropertyintegration.model;

import java.util.Date;
import lombok.Builder;

public class GetPaidInvoicePaymentHistoryRequestModel extends PaginatedRequestModel {

    private Date fromTimestamp;
    private Date toTimestamp;

    @Builder(builderMethodName = "getPaidInvoicePaymentHistoryRequestModelBuilder")
    public GetPaidInvoicePaymentHistoryRequestModel(int pageSize, int currentPage, Date fromTimestamp,
            Date toTimestamp) {
        super(pageSize, currentPage);
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
    }
}
