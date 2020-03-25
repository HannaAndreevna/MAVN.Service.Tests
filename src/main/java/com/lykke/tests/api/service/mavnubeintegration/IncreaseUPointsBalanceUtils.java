package com.lykke.tests.api.service.mavnubeintegration;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.MAVNUbeIntegrationService.INCREASE_UPOINT_BALANCE_PATH;
import static com.lykke.tests.api.base.Paths.MVN_UBE_INTEGRATION_API_PATH;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class IncreaseUPointsBalanceUtils {

    private static final String CUSTOMER_ID = "CustomerId";
    private static final String CHANGE_AMOUNT = "ChangeAmount";

    public Response increaseUPointsBalance(String customerId, Double changeAmount) {
        return getHeader()
                .body(increaseUPointsBalanceObject(customerId, changeAmount))
                .post(MVN_UBE_INTEGRATION_API_PATH + INCREASE_UPOINT_BALANCE_PATH.getPath());
    }

    private static JSONObject increaseUPointsBalanceObject(String customerId, Double changeAmount) {
        JSONObject increaseUPointsBalanceObject = new JSONObject();
        increaseUPointsBalanceObject.put(CUSTOMER_ID, customerId);
        increaseUPointsBalanceObject.put(CHANGE_AMOUNT, changeAmount);
        return increaseUPointsBalanceObject;
    }
}
