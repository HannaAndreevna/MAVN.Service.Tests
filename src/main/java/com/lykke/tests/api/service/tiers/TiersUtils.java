package com.lykke.tests.api.service.tiers;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import com.lykke.tests.api.base.Paths.Tiers;
import com.lykke.tests.api.service.tiers.model.TierModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TiersUtils {

    public Response getCustomerTier(String customerId) {
        return getHeader()
                .get(Tiers.CUSTOMERS_BY_ID_TIER_API_PATH.apply(customerId));
    }

    public Response getReports() {
        return getHeader()
                .get(Tiers.REPORTS_NUMBER_OF_CUSTOMERS_PER_TIER_API_PATH);
    }

    public Response getTiers() {
        return getHeader()
                .get(Tiers.TIERS_API_PATH);
    }

    public Response putTier(TierModel requestModel) {
        return getHeader()
                .body(requestModel)
                .put(Tiers.TIERS_API_PATH);
    }

    public Response getTierById(String tierId) {
        return getHeader()
                .get(Tiers.TIER_BY_ID_API_PATH.apply(tierId));
    }
}
