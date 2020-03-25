package com.lykke.tests.api.service.mavnubeintegration;

import com.lykke.api.testing.api.base.RequestHeader;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

import java.util.UUID;

import static com.lykke.tests.api.base.PathConsts.MAVNUbeIntegrationService.UPOINTS_BALANCE_PATH;
import static com.lykke.tests.api.base.Paths.MVN_UBE_INTEGRATION_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@UtilityClass
public class UPointsBalanceUtils {
    private static final String OPERATION_ID = "OperationId";
    private static final String CUSTOMER_ID = "CustomerId";
    private static final String AMOUNT = "Amount";
    private static final String CHANGE_DATE = "ChangeDate";

    public Response executeUPointsBalance(UPointsBalance balance) {
        return getHeader()
                .body(uPointsBalanceObject(balance))
                .post(MVN_UBE_INTEGRATION_API_PATH + UPOINTS_BALANCE_PATH.getPath());
    }

    private static JSONObject uPointsBalanceObject(UPointsBalance balance) {
        JSONObject uPointsBalanceObject = new JSONObject();
        uPointsBalanceObject.put(OPERATION_ID, balance.getOperationId());
        uPointsBalanceObject.put(CUSTOMER_ID, balance.getCustomerId());
        uPointsBalanceObject.put(AMOUNT, balance.getAmount());
        uPointsBalanceObject.put(CHANGE_DATE, balance.getChangeDate());
        return uPointsBalanceObject;
    }
}
