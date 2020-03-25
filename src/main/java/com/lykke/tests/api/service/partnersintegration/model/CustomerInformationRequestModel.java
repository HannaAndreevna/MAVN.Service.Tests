package com.lykke.tests.api.service.partnersintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CustomerInformationRequestModel {

    private String id;
    private String email;
    private String phone;
}
