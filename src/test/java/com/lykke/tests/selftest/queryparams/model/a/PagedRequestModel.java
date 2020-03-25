package com.lykke.tests.selftest.queryparams.model.a;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@PublicApi
@QueryParameters
public class PagedRequestModel {

    private int pageSize;
    private int currentPage;
}