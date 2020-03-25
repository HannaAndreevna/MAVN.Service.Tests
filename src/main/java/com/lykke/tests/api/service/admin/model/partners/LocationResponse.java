package com.lykke.tests.api.service.admin.model.partners;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {

    private String id;
    private String name;
    private String address;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
