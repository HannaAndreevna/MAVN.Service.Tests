package com.lykke.tests.api.service.operationshistory.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;

@Data
@QueryParameters
public class PaginationModelWithDatesRange extends PaginationModel {

    private String fromDate;
    private String toDate;

    @Builder(builderMethodName = "paginationModelWithDatesRangeBuilder")
    public PaginationModelWithDatesRange(int currentPage, int pageSize, String fromDate, String toDate) {
        super(currentPage, pageSize);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
