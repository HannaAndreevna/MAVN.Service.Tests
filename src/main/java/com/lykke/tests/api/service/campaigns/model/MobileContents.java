package com.lykke.tests.api.service.campaigns.model;

import com.lykke.tests.api.common.enums.Localization;
import com.lykke.tests.api.service.campaigns.model.burnrules.RuleContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class MobileContents {

    private String titleId;
    private String descriptionId;
    private String imageId;
    private Localization mobileLanguage;
    private RuleContentType title;
    private String description;
}
