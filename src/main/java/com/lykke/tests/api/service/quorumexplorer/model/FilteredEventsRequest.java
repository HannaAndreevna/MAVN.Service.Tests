package com.lykke.tests.api.service.quorumexplorer.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class FilteredEventsRequest {

    private String eventName;
    private String eventSignature;
    private String address;
    private String[] affectedAddresses;
    private PaginationModel pagingInfo;
}
