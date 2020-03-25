package com.lykke.tests.api.service.mavnubeintegration;

import com.lykke.api.testing.api.base.RequestHeader;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

import static com.lykke.tests.api.base.PathConsts.MAVNUbeIntegrationService.DECREASE_UPOINT_BALANCE_PATH;
import static com.lykke.tests.api.base.Paths.MVN_UBE_INTEGRATION_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@UtilityClass
public class DecreaseUPointsBalanceUtils {

    private static final String CUSTOMER_ID = "CustomerId";
    private static final String CHANGE_AMOUNT = "ChangeAmount";

    public Response decreaseUPointsBalance(String customerId, Double changeAmount) {
        return getHeader()
                .body(decreaseUPointsBalanceObject(customerId, changeAmount))
                .post(MVN_UBE_INTEGRATION_API_PATH + DECREASE_UPOINT_BALANCE_PATH.getPath());
    }

    private static JSONObject decreaseUPointsBalanceObject(String customerId, Double changeAmount) {
        JSONObject decreaseUPointsBalanceObject = new JSONObject();
        decreaseUPointsBalanceObject.put(CUSTOMER_ID, customerId);
        decreaseUPointsBalanceObject.put(CHANGE_AMOUNT, changeAmount);
        return decreaseUPointsBalanceObject;
    }
}
