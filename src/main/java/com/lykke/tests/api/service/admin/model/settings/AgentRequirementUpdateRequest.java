package com.lykke.tests.api.service.admin.model.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AgentRequirementUpdateRequest {

    private String tokensAmount;
}
