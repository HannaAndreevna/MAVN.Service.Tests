package com.lykke.tests.selftest.queryparams.model.b;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class EventListRequest {

    private PagedRequestModel pagedRequest;
    private String eventName;
    private String eventSignature;
    private String address;
    private String[] affectedAddresses;

}
