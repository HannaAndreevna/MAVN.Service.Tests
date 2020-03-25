package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Customer.PIN_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.PIN_CHECK_API_PATH;

import com.lykke.tests.api.service.customer.model.pin.PinRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PinUtils {

    public Response postPin(PinRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PIN_API_PATH)
                .thenReturn();
    }

    public Response postCheckPin(PinRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PIN_CHECK_API_PATH)
                .thenReturn();
    }

    public Response changePin(PinRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .put(PIN_API_PATH)
                .thenReturn();
    }
}
