package com.lykke.tests.api.service.credentials;

import static com.lykke.tests.api.service.credentials.PinUtils.changePin;
import static com.lykke.tests.api.service.credentials.PinUtils.getHasPin;
import static com.lykke.tests.api.service.credentials.PinUtils.posValidatePin;
import static com.lykke.tests.api.service.credentials.PinUtils.postPin;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.credentials.model.pin.HasPinResponse;
import com.lykke.tests.api.service.credentials.model.pin.PinCodeErrorCode;
import com.lykke.tests.api.service.credentials.model.pin.SetPinRequest;
import com.lykke.tests.api.service.credentials.model.pin.SetPinResponse;
import com.lykke.tests.api.service.credentials.model.pin.ValidatePinRequest;
import com.lykke.tests.api.service.credentials.model.pin.ValidatePinResponse;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PinTests extends BaseApiTest {

    private static final String SAMPLE_PIN = "1234";
    private static final String SAMPLE_NEW_PIN = "7821";
    private CustomerInfo customerData;

    @BeforeEach
    void setUp() {
        customerData = registerDefaultVerifiedCustomer();
    }

    @Test
    @UserStoryId(3949)
    void shouldSetPin() {
        val expectedResult = SetPinResponse
                .builder()
                .error(PinCodeErrorCode.NONE)
                .build();

        val actualResult = postPin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3949)
    void shouldNotSetPinTwice() {
        val expectedResult = SetPinResponse
                .builder()
                .error(PinCodeErrorCode.PIN_ALREADY_SET)
                .build();

        postPin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        val actualResult = postPin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3949)
    void shouldValidatePin() {
        postPin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        val expectedResult = ValidatePinResponse
                .builder()
                .error(PinCodeErrorCode.NONE)
                .build();

        val actualResult = posValidatePin(ValidatePinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ValidatePinResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3949)
    void shouldCheckHasPin() {
        val expectedResult01 = HasPinResponse
                .builder()
                .hasPin(false)
                .build();

        val actualResult01 = getHasPin(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(HasPinResponse.class);

        assertEquals(expectedResult01, actualResult01);

        postPin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        val expectedResult02 = HasPinResponse
                .builder()
                .hasPin(true)
                .build();

        val actualResult02 = getHasPin(customerData.getCustomerId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(HasPinResponse.class);

        assertEquals(expectedResult02, actualResult02);
    }

    @Test
    @UserStoryId(4195)
    void shouldChangePin() {
        val expectedResult01 = SetPinResponse
                .builder()
                .error(PinCodeErrorCode.NONE)
                .build();

        val actualResult01 = postPin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        assertEquals(expectedResult01, actualResult01);

        val expectedResult02 = SetPinResponse
                .builder()
                .error(PinCodeErrorCode.NONE)
                .build();

        val actualResult02 = changePin(SetPinRequest
                .builder()
                .pinCode(SAMPLE_NEW_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(SetPinResponse.class);

        assertEquals(expectedResult02, actualResult02);

        val expectedResult03 = ValidatePinResponse
                .builder()
                .error(PinCodeErrorCode.NONE)
                .build();

        val actualResult03 = posValidatePin(ValidatePinRequest
                .builder()
                .pinCode(SAMPLE_NEW_PIN)
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ValidatePinResponse.class);

        assertEquals(expectedResult03, actualResult03);
    }
}
