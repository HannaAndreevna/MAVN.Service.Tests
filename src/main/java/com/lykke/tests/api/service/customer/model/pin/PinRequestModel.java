package com.lykke.tests.api.service.customer.model.pin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PinRequestModel {

    private String pin;
}
