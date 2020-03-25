package com.lykke.tests.api.service.customerprofile.model.partnercontacts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PartnerContactsByLocationRequestModel {
    private String locationId;
}
