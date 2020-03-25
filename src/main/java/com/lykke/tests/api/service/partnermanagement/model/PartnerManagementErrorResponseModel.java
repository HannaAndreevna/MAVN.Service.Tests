package com.lykke.tests.api.service.partnermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PartnerManagementErrorResponseModel {

    private PartnerManagementError errorCode;
    private String errorMessage;
}
