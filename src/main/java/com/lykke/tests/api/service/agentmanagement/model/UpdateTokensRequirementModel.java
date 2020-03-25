package com.lykke.tests.api.service.agentmanagement.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class UpdateTokensRequirementModel {

    private String amount;
}