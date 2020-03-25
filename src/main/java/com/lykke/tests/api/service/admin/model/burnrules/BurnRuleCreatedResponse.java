package com.lykke.tests.api.service.admin.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BurnRuleCreatedResponse {

    private String id;
    private ImageContentCreatedResponse[] createdImageContents;

    public BurnRuleCreatedResponse() {
        createdImageContents = new ImageContentCreatedResponse[]{};
    }
}
