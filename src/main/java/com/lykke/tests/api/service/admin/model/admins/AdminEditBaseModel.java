package com.lykke.tests.api.service.admin.model.admins;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AdminEditBaseModel {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String company;
    private String department;
    private String jobTitle;
}
