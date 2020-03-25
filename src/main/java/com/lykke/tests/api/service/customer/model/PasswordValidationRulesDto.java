package com.lykke.tests.api.service.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordValidationRulesDto {

    private int minLength;
    private int maxLength;
    private int minUpperCase;
    private int minLowerCase;
    private int minSpecialSymbols;
    private int minNumbers;
    private String allowedSpecialSymbols;
    private boolean allowWhiteSpaces;
}
