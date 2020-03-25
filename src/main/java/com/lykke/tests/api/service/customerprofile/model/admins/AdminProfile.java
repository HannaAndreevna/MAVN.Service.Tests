package com.lykke.tests.api.service.customerprofile.model.admins;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class AdminProfile {

    private String adminId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String company;
    private String department;
    private String jobTitle;
}
