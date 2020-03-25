package com.lykke.tests.api.service.notificationsystem.templates.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewTemplateRequest {

    private String templateName;
    private String templateBody;
    private String localizationCode;
}
