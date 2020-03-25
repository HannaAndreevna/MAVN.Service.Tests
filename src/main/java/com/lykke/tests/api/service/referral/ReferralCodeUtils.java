package com.lykke.tests.api.service.referral;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_GET_REFERRAL_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_POST_REFERRAL_PATH;
import static com.lykke.tests.api.base.Paths.REFERRAL_API_REFERRAL_PATH;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.base.PathConsts.ReferralService;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class ReferralCodeUtils {

    public static final String CUSTOMER_ID_FIELD = "CustomerId";

    public String getReferralCode(String customerId) {
        return getReferralCodeByCustomerId(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("ReferralCode");
    }

    public Response getReferralCodeByCustomerId(String customerId) {
        return getHeader()
                .get(REFERRAL_API_GET_REFERRAL_PATH + customerId);
    }

    public Response getReferralInfoByCustomerId(String customerId) {
        return getHeader()
                .get(REFERRAL_API_REFERRAL_PATH + ReferralService.FRIENDS_BY_CUSTOMER_ID_PATH
                        .getFilledInPath(customerId));
    }

    public Response setReferralCodeByCustomerId(String customerId) {
        return given()
                .contentType(JSON)
                .when()
                .body(customerObject(customerId))
                .post(REFERRAL_API_POST_REFERRAL_PATH);
    }

    private static JSONObject customerObject(String customerId) {
        JSONObject balanceObject = new JSONObject();
        balanceObject.put(CUSTOMER_ID_FIELD, customerId);
        return balanceObject;
    }
}
