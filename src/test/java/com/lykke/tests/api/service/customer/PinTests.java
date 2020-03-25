package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.PinUtils.changePin;
import static com.lykke.tests.api.service.customer.PinUtils.postCheckPin;
import static com.lykke.tests.api.service.customer.PinUtils.postPin;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import com.lykke.tests.api.service.credentials.model.pin.PinCodeErrorCode;
import com.lykke.tests.api.service.customer.model.pin.PinRequestModel;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PinTests extends BaseApiTest {

    private static final String SAMPLE_PIN = "5678";
    private static final String SAMPLE_NEW_PIN = "0284";
    private static final String SAMPLE_PIN_02 = "0145";
    private static final String THE_PROVIDED_PIN_DOES_NOT_MATCH_THE_CUSTOMER_S_ONE_ERROR_MESSAGE = "The provided PIN does not match the customer's one";
    private CustomerInfo customerData;
    private String customerToken;

    static Stream<Arguments> getInvalidPinData() {
        return Stream.of(
                of(EMPTY),
                of("1"),
                of("12345"),
                of("abcd"),
                of("ABCD")
        );
    }

    @BeforeEach
    void setUp() {
        customerData = registerDefaultVerifiedCustomer();
        customerToken = getUserToken(customerData);
    }

    @Test
    @UserStoryId(3951)
    void shouldSetPin() {
        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(3951)
    void shouldNotSetPinTwice() {
        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPinData")
    @UserStoryId(3951)
    void shouldNotSetInvalidPin(String pin) {
        postPin(PinRequestModel
                .builder()
                .pin(pin)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(3951)
    void shouldCheckPin() {
        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        postCheckPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(3951)
    void shouldReportWrongPin() {
        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val expectedResult = ValidationErrorResponseModel
                .builder()
                .error(PinCodeErrorCode.PIN_CODE_MISMATCH.getCode())
                .message(THE_PROVIDED_PIN_DOES_NOT_MATCH_THE_CUSTOMER_S_ONE_ERROR_MESSAGE)
                .build();

        val actualResult = postCheckPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN_02)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(4193)
    void shouldChangePin() {
        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        postCheckPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        changePin(PinRequestModel
                .builder()
                .pin(SAMPLE_NEW_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        postCheckPin(PinRequestModel
                .builder()
                .pin(SAMPLE_NEW_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(4193)
    void shouldNotChangePinToTheSameValue() {
        postPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        postCheckPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        changePin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        postCheckPin(PinRequestModel
                .builder()
                .pin(SAMPLE_PIN)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }
}
