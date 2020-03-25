package com.lykke.tests.api.service.adminmanagement.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class AuthenticateRequestModel {

    private String email;
    private String password;
}
