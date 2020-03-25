package com.lykke.tests.api.service.credentials.model.pin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SetPinRequest {

    private String customerId;
    private String pinCode;
}
