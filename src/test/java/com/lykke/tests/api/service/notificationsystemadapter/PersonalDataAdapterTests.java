package com.lykke.tests.api.service.notificationsystemadapter;

import static com.lykke.tests.api.base.Credentials.ADMIN_TEST_USER_ID;
import static com.lykke.tests.api.base.Paths.NOTIFICATION_ADAPTER_KEYS_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.COMMON_INFORMATION_NAMESPACE_VALUE;
import static com.lykke.tests.api.common.CommonConsts.EMAIL_FIELD;
import static com.lykke.tests.api.common.CommonConsts.NOTIFICATION_ADAPTER_API_KEY;
import static com.lykke.tests.api.common.CommonConsts.PERSONAL_DATA_NAMESPACE_VALUE;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.notificationsystemadapter.PersonalDataAdapterUtils.getKeysByCustomerIdAndNamespace;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import java.util.stream.Stream;
import lombok.var;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PersonalDataAdapterTests extends BaseApiTest {

    private static final String LOCALIZATION_FIELD = "Localization";
    private static final String DOWNLOAD_APP_FIELD = "DownloadAndroidAppLink";
    private static final String DOWNLOAD_ISO_APP_FIELD = "DownloadIsoAppLink";
    private static final String SUPPORT_EMAIL_FIELD = "SupportEmailAddress";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String INVALID_VALUE_FOR_NAMESPACE_MESSAGE = "Invalid value for namespace";
    private static final String invalidCustomerId = "640e4655-4f6f-45d6-a663-c585deed30cd";
    private static final String INVALID_NAMESPACE = "invalidNamespace";
    private static final String NAME_FIELD = "name";
    private static final String FIRST_NAME_FIELD = "FirstName";
    private static final String LAST_NAME_FIELD = "LastName";
    private static final String GOOGLE_PLAY_URL = "https://play.google.com";
    private static final String ITUNES_URL = "itunes.apple.com";
    private static final String TOKENSUPPORT_ADDRESS = "tokensupport@mavn.com";

    private static Stream invalidParameters_namespace() {
        return Stream.of(
                of(null, INVALID_VALUE_FOR_NAMESPACE_MESSAGE),
                of(INVALID_NAMESPACE, INVALID_VALUE_FOR_NAMESPACE_MESSAGE)
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {531, 1465, 3238})
    void shouldGetCustomerEmailAndLocalization() {
        var customerData = registerDefaultVerifiedCustomer();
        getKeysByCustomerIdAndNamespace(customerData.getCustomerId(), PERSONAL_DATA_NAMESPACE_VALUE,
                NOTIFICATION_ADAPTER_KEYS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(EMAIL_FIELD, equalTo(customerData.getEmail()))
                .body(FIRST_NAME_FIELD, equalTo(customerData.getFirstName()))
                .body(LAST_NAME_FIELD, equalTo(customerData.getLastName()))
                .body(LOCALIZATION_FIELD, equalTo("en")); // only type for now
    }

    @UserStoryId(storyId = 531)
    @ParameterizedTest(name = "Run {index}: namespaceValue={0}, messageText={1}")
    @MethodSource("invalidParameters_namespace")
    void shouldNotCustomerEmailAndLocalizationWhenNamespaceIsNotValid(String namespaceValue, String messageText) {
        getKeysByCustomerIdAndNamespace(ADMIN_TEST_USER_ID, namespaceValue, NOTIFICATION_ADAPTER_KEYS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(messageText));
    }

    @Test
    @UserStoryId(storyId = 531)
    void shouldNotGetCustomerEmailAndLocalizationWhenCustomerIdIsNotValid() {
        getKeysByCustomerIdAndNamespace(invalidCustomerId, PERSONAL_DATA_NAMESPACE_VALUE,
                NOTIFICATION_ADAPTER_KEYS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(equalTo("{}"));
    }

    @Test
    @UserStoryId(3544)
    void shouldContainAdditionalProperties() {
        var customerId = registerCustomer();
        getKeysByCustomerIdAndNamespace(customerId, COMMON_INFORMATION_NAMESPACE_VALUE,
                NOTIFICATION_ADAPTER_KEYS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(DOWNLOAD_APP_FIELD, containsString(GOOGLE_PLAY_URL))
                .body(DOWNLOAD_ISO_APP_FIELD, containsString(ITUNES_URL))
                .body(SUPPORT_EMAIL_FIELD, containsString(TOKENSUPPORT_ADDRESS)); // only type for now
    }
}
