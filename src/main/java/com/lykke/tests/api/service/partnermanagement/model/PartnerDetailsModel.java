package com.lykke.tests.api.service.partnermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerDetailsModel extends PartnerBaseModel {

    private String id;
    private LocationDetailsModel[] locations;
    private String clientId;
    private String createdBy;
    private Date createdAt;
}
