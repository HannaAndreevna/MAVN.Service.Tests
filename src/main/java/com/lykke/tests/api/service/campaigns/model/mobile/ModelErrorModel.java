package com.lykke.tests.api.service.campaigns.model.mobile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelErrorModel {
    private String[] burnRuleId;
    private String[] earnRuleId;
}
