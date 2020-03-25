package com.lykke.tests.api.service.operationshistory.model.Statistics;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class CustomersByDateResponseModel {

    private ActiveCustomersModel[] activeCustomers;
    private int totalActiveCustomers;
}
