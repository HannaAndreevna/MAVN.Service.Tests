package com.lykke.tests.api.service.campaigns.model;

import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.common.enums.RuleContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class EarnRuleContentEditRequest {

    private String id;
    private RuleContentType ruleContentType;
    private Localization localization;
    private String value;
}
