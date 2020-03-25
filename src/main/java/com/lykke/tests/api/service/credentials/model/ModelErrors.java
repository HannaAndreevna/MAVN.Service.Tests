package com.lykke.tests.api.service.credentials.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ModelErrors {

    private String[] clientId;
    private String[] clientSecret;
}
