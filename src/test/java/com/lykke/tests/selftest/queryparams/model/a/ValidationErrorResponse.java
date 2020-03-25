package com.lykke.tests.selftest.queryparams.model.a;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorResponse {

    private String[] pageSize;
    private String[] currentPage;
    @JsonProperty("error")
    private String error;
    @JsonProperty("message")
    private String message;
    private String[] customerId;
}

