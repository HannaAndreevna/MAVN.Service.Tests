package com.lykke.tests.api.service.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class CustomerModel {

    private String customerId;
    private String email;
    private boolean isEmailVerified;
    private String phoneNumber;
    private boolean isPhoneVerified;
    private String firstName;
    private String lastName;
    @Exclude
    private Date registeredDate;
    private String referralCode;
    private CustomerActivityStatus customerStatus;
    private CustomerAgentStatus customerAgentStatus;
}
