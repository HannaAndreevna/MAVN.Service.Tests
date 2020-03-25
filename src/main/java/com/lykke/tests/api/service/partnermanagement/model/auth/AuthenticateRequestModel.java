package com.lykke.tests.api.service.partnermanagement.model.auth;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class AuthenticateRequestModel {

    private String clientId;
    private String clientSecret;
    private String userInfo;
}
