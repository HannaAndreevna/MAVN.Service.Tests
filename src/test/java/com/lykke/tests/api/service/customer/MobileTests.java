package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.service.customer.MobileUtils.getMobileSettings;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import org.junit.jupiter.api.Test;

public class MobileTests extends BaseApiTest {

    private static final String PIN_CODE_LENGTH_FIELD_PATH = "PinCode.PinCodeLength";
    private static final String PIN_CODE_WARNING_ATTEMPT_COUNT_FIELD_PATH = "PinCode.PinCodeWarningAttemptCount";
    private static final String PIN_CODE_MAXIMUM_ATTEMPT_COUNT_FIELD_PATH = "PinCode.PinCodeMaximumAttemptCount";
    private static final int PIN_CODE_LENGTH = 4;
    private static final int PIN_CODE_WARNING_ATTEMPT_COUNT = 3;
    private static final int PIN_CODE_MAXIMUM_ATTEMPT_COUNT = 5;

    @Test
    @UserStoryId(4194)
    void shouldGetMobileSettings() {
        getMobileSettings()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(PIN_CODE_LENGTH_FIELD_PATH, equalTo(PIN_CODE_LENGTH))
                .body(PIN_CODE_WARNING_ATTEMPT_COUNT_FIELD_PATH, equalTo(PIN_CODE_WARNING_ATTEMPT_COUNT))
                .body(PIN_CODE_MAXIMUM_ATTEMPT_COUNT_FIELD_PATH, equalTo(PIN_CODE_MAXIMUM_ATTEMPT_COUNT));
    }
}
