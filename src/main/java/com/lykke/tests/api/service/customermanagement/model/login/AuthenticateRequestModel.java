package com.lykke.tests.api.service.customermanagement.model.login;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@JsonNaming(UpperCamelCaseStrategy.class)
public class AuthenticateRequestModel {
    private String email;
    private String password;
    private String loginProvider;
}
