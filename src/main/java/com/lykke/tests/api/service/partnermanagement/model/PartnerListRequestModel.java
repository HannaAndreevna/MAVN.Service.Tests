package com.lykke.tests.api.service.partnermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PartnerListRequestModel {

    private int currentPage;
    private int pageSize;
    private String name;
    private Vertical vertical;
}
