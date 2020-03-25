package com.lykke.tests.api.service.customerprofile.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CustomerStatisticsRequestModel {

    private String startDate;
    private String endDate;
}
