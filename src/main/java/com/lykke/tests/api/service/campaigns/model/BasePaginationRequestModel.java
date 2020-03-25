package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class BasePaginationRequestModel {

    private int currentPage;
    private int pageSize;
}
