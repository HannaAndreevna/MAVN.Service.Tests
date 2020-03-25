package com.lykke.tests.api.service.adminmanagement.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class RegistrationRequestModel {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String company;
    private String department;
    private String jobTitle;
    private AdminPermission[] permissions;

    public RegistrationRequestModel() {
        permissions = new AdminPermission[]{};
    }
}
