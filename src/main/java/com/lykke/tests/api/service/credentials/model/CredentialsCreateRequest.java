package com.lykke.tests.api.service.credentials.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class CredentialsCreateRequest extends Credentials {

    private String customerId;

    @Builder(builderMethodName = "credentialsUpdateRequestBuilder")
    public CredentialsCreateRequest(String clientId, String clientSecret, String customerId) {
        super(clientId, clientSecret);
        this.customerId = customerId;
    }
}
