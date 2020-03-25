package com.lykke.tests.api.service.quorumexplorer.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@QueryParameters
public class PaginationModel {

    private int currentPage;
    private int pageSize;
}
