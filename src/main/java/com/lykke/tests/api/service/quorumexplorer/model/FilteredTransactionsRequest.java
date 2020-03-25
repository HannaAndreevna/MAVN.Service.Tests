package com.lykke.tests.api.service.quorumexplorer.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class FilteredTransactionsRequest {

    private String functionName;
    private String functionSignature;
    private String[] from;
    private String[] to;
    private String[] affectedAddresses;
    private PaginationModel pagingInfo;
}
