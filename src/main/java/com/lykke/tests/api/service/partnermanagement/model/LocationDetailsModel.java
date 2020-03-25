package com.lykke.tests.api.service.partnermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDetailsModel extends LocationBaseModel {

    private String id;
    private String createdBy;
    private Date createdAt;

    @Builder(builderMethodName = "locationDetailsModelBuilder")
    public LocationDetailsModel(String name, String address, String externalId, ContactPersonModel contactPerson,
            String accountingIntegrationCode, String id, String createdBy, Date createdAt) {
        super(name, address, externalId, contactPerson, accountingIntegrationCode);
        this.id = id;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
