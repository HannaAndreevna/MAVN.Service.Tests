package com.lykke.tests.api.service.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(LowerCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LykkeApiErrorResponse {

    private String error;
    private String message;
}

