package com.lykke.tests.api.service.customerprofile.model.admins;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class AdminProfileRequest {

    private String adminId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String company;
    private String department;
    private String jobTitle;
}