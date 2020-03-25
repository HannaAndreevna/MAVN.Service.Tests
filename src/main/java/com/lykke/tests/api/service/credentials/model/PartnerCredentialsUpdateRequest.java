package com.lykke.tests.api.service.credentials.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.Builder;
import lombok.Data;

@Data
@PublicApi
public class PartnerCredentialsUpdateRequest extends PartnerCredentials {

    private String partnerId;

    @Builder(builderMethodName = "credentialsUpdateRequestBuilder")
    public PartnerCredentialsUpdateRequest(String clientId, String clientSecret, String partnerId) {
        super(clientId, clientSecret);
        this.partnerId = partnerId;
    }
}
