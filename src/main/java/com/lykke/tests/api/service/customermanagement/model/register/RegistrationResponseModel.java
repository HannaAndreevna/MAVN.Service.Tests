package com.lykke.tests.api.service.customermanagement.model.register;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class RegistrationResponseModel {
    private String customerId;
    private String error;
}
