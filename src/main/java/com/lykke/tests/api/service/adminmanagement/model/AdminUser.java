package com.lykke.tests.api.service.adminmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class AdminUser {

    private String adminUserId;
    private boolean isActive;
    private String email;
    private String firstName;
    private String lastName;
    @Exclude
    private Date registeredAt;
    private String phoneNumber;
    private String company;
    private String department;
    private String jobTitle;
    private AdminPermission[] permissions;
}
