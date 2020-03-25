package com.lykke.tests.api.service.credentials;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.Credentials.HAS_PIN_API_PATH;
import static com.lykke.tests.api.base.Paths.Credentials.PIN_API_PATH;
import static com.lykke.tests.api.base.Paths.Credentials.VALIDATE_PIN_API_PATH;

import com.lykke.tests.api.service.credentials.model.pin.SetPinRequest;
import com.lykke.tests.api.service.credentials.model.pin.ValidatePinRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PinUtils {

    public Response postPin(SetPinRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(PIN_API_PATH)
                .thenReturn();
    }

    public Response changePin(SetPinRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .put(PIN_API_PATH)
                .thenReturn();
    }

    public Response posValidatePin(ValidatePinRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(VALIDATE_PIN_API_PATH)
                .thenReturn();
    }

    public Response getHasPin(String customerId) {
        return getHeader()
                .queryParam("customerId", customerId)
                .get(HAS_PIN_API_PATH)
                .thenReturn();
    }
}
