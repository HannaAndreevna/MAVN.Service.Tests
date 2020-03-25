package com.lykke.tests.api.service.bonusengine;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.BonusEngine.CAMPAIGN_COMPLETION_API_PATH;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BonusEngineUtils {

    public Response getCampaignCompletion(String customerId, String campaignId) {
        return getHeader()
                .get(CAMPAIGN_COMPLETION_API_PATH.apply(customerId, campaignId))
                .thenReturn();
    }
}
