package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private Campaign[] campaigns;
}
