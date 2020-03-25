package com.lykke.tests.api.service.walletmanagement.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class BlockUnblockRequestModel {

    private String customerId;
}
