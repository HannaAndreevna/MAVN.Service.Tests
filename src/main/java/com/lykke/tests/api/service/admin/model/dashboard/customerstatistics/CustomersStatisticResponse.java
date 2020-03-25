package com.lykke.tests.api.service.admin.model.dashboard.customerstatistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomersStatisticResponse {

    private int totalActiveCustomers;
    private int totalNonActiveCustomers;
    private int totalCustomers;
    private int totalNewCustomers;
    private CustomerStatisticsByDayResponse[] newCustomers;
}
