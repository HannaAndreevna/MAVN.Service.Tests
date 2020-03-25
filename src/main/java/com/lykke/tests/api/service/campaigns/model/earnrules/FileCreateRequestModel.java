package com.lykke.tests.api.service.campaigns.model.earnrules;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@JsonNaming(UpperCamelCaseStrategy.class)
public class FileCreateRequestModel {
    private String ruleContentId;
    private String name;
    private String type;
    private String content;
}
