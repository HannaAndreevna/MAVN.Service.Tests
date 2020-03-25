package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@PublicApi
@NetClassName("BurnRuleContentEditRequest")
public class BurnRuleContentEditRequestModel {

    private String id;
    private RuleContentType ruleContentType;
    private Localization localization;
    private String value;
}
