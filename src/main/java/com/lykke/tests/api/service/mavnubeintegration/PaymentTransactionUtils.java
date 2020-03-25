package com.lykke.tests.api.service.mavnubeintegration;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

import static com.lykke.tests.api.base.PathConsts.MAVNUbeIntegrationService.PAYMENT_TRANSACTION_PATH;
import static com.lykke.tests.api.base.Paths.MVN_UBE_INTEGRATION_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

@UtilityClass
public class PaymentTransactionUtils {

    private static final String OPERATION_ID = "OperationId";
    private static final String CUSTOMER_ID = "CustomerId";
    private static final String VENUE_ID = "VenueId";
    private static final String AMOUNT = "Amount";
    private static final String PAYMENT_DATE = "PaymentDate";

    public Response executePaymentTransaction(PaymentTransaction paymentTransaction) {
        return getHeader()
                .body(paymentTransactionObject(paymentTransaction))
                .post(MVN_UBE_INTEGRATION_API_PATH + PAYMENT_TRANSACTION_PATH.getPath());
    }

    private static JSONObject paymentTransactionObject(PaymentTransaction paymentTransaction) {
        JSONObject paymentTransactionObject = new JSONObject();
        paymentTransactionObject.put(OPERATION_ID, paymentTransaction.getOperationId());
        paymentTransactionObject.put(CUSTOMER_ID, paymentTransaction.getCustomerId());
        paymentTransactionObject.put(VENUE_ID, paymentTransaction.getVenueId());
        paymentTransactionObject.put(AMOUNT, paymentTransaction.getAmount());
        paymentTransactionObject.put(PAYMENT_DATE, paymentTransaction.getPaymentDate());
        return paymentTransactionObject;
    }
}
