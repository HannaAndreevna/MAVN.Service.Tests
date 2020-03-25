package com.lykke.tests.api.service.bonusengine.purchaseproducttriggers;

import static com.lykke.tests.api.base.Paths.MVN_PURCHASE_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class MVNIntegrationUtils {

    private static final String AMOUNT_FIELD = "Amount";
    private static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static final String CURRENCY_FIELD = "Currency";
    private static final String REFERRAL_CODE_FIELD = "ReferralCode";

    public Response makePurchaseWithReferral(String customerId, Float amount, String currency, String referralCode) {
        return getHeader()
                .body(createPurchaseObjectWithReferralCode(customerId, amount, currency, referralCode))
                .post(MVN_PURCHASE_API_PATH);
    }

    public Response makePurchaseWithoutReferral(String customerId, Float amount, String currency) {
        return getHeader()
                .body(createPurchaseObject(customerId, amount, currency))
                .post(MVN_PURCHASE_API_PATH);
    }

    private static JSONObject createPurchaseObject(String customerId, Float amount, String currency) {
        JSONObject purchaseObject = new JSONObject();
        purchaseObject.put(CUSTOMER_ID_FIELD, customerId);
        purchaseObject.put(CURRENCY_FIELD, currency);
        purchaseObject.put(AMOUNT_FIELD, amount);
        return purchaseObject;
    }

    private static JSONObject createPurchaseObjectWithReferralCode(String customerId, Float amount, String currency, String referralCode) {
        JSONObject purchaseObject = createPurchaseObject(customerId, amount, currency);
        purchaseObject.put(REFERRAL_CODE_FIELD, referralCode);
        return purchaseObject;
    }
}
