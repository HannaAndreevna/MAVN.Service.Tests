package com.lykke.tests.api.service.bonuscustomerprofile.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignContribution {
    private UUID id;
    private String customerId;
    private String campaignId;
}
