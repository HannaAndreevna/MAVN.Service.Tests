package com.lykke.tests.api.service.admin.model.blockchain;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class TransactionListRequest {

    private PagedRequestModel pagedRequest;
    private String functionName;
    private String functionSignature;
    private String[] from;
    private String[] to;
    private String[] affectedAddresses;
}
