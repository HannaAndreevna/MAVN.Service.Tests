package com.lykke.tests.api.service.notificationsystem.templates.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class TemplateByLanguageResponse {
    private TemplateByLanguageDetailsResponse template;
}
