package com.lykke.tests.api.service.admin.model.blockchain;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class BlockListRequest {

    private int pageSize;
    private int currentPage;
}
