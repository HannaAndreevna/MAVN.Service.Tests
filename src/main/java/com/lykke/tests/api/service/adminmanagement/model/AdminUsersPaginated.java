package com.lykke.tests.api.service.adminmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AdminUsersPaginated {

    private int currentPage;
    private int pageSize;
}
