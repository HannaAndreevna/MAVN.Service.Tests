package com.lykke.tests.api.service.notificationsystem.templates.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@NetClassName("TemplateInfoDto")
public class TemplateResponse {

    private String templateName;
    private AvailableLocalizationResponse[] availableLocalizations;

    public TemplateResponse() {
        availableLocalizations = new AvailableLocalizationResponse[]{};
    }
}
