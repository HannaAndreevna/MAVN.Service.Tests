package com.lykke.tests.api.service.dashboardstatistics.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ValidationErrorDashboardStatisticsResponse {
    private String errorMessage;
    private ModelErrorModel modelErrors;

    public ValidationErrorDashboardStatisticsResponse() {
        modelErrors = new ModelErrorModel(); }
}
