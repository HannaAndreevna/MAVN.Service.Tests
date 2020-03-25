package com.lykke.tests.api.service.admin.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@PublicApi
@QueryParameters
public class PagedRequestModel {

    private int pageSize;
    private int currentPage;
}