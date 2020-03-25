package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class FileResponseModel {

    private String id;
    private String ruleContentId;
    private String name;
    private String type;
    private String blobUrl;
}
