package com.lykke.tests.api.service.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralsLeadModel {

    private String name;
    private String number;
    private String email;
    private String note;
}
