package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PaginatedRequestModel {

    //// currentPage, pageSize
    private int pageSize;
    private int currentPage;
}
