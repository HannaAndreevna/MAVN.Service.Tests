package com.lykke.tests.api.service.credentials.model;

import com.lykke.api.testing.annotations.NetClassName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@NetClassName("none")
public class PartnerCredentialsRemoveRequest {

    private String partnerId;
}
