package com.lykke.tests.api.service.mavnpropertyintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AgentImageRequestModel {

    private ImageDocumentType documentType;
    private String imageName;
    private String imageBase64;
}
