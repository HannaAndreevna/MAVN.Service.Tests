package com.lykke.tests.api.service.admin.model.blockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaginationModel {

    private int currentPage;
    private int pageSize;
}
