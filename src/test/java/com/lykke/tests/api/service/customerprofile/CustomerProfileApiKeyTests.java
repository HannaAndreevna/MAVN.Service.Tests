package com.lykke.tests.api.service.customerprofile;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.API_KEY;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.createCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.createCustomerProfileWithoutApiKey;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.deleteCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.deleteCustomerProfileWithoutApiKey;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomerProfileWithoutApiKey;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getPagination;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getPaginationWithoutApiKey;
import static com.lykke.tests.api.service.customerprofile.model.LoginProvider.STANDARD;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CustomerProfileApiKeyTests extends BaseApiTest {

    private static final String ERROR_CODE_PATH = "ErrorCode";
    private static final String CUSTOMER_PROFILE_NOT_FOUND_ERR_CODE = "CustomerProfileDoesNotExist";
    private static final String NONE_ERR_CODE = "None";
    private static final String CUSTOMER_ID_FIELD = "Profile.CustomerId";
    private static final String CUSTOMERS_FIELD = "Customers";
    private static final String VALID_API_KEY = API_KEY;
    private static final String INVALID_API_KEY_01 = EMPTY;
    private static final String INVALID_API_KEY_02 = "key1";

    private String customerId;
    private String token;

    private static Stream<Arguments> getCustomerProfilePagination_Valid() {
        return Stream.of(
                Arguments.of(2, 1)
        );
    }

    private static Stream<Arguments> getCustomerProfilePagination_InvalidKey() {
        return Stream.of(
                Arguments.of(1, 2, INVALID_API_KEY_01),
                Arguments.of(1, 2, INVALID_API_KEY_02),
                Arguments.of(2, 1, INVALID_API_KEY_01),
                Arguments.of(2, 1, INVALID_API_KEY_02)
        );
    }

    @BeforeEach
    void methodSetup() {
        var customer = new RegistrationRequestModel();
        customerId = registerCustomer(customer);
        token = getUserToken(customer.getEmail(), customer.getPassword());
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId({709, 1542})
    void shouldCreateAndGetCustomerProfileWithKey() {
        val custId = getRandomUuid();
        createCustomerProfile(VALID_API_KEY, custId, generateRandomEmail(), FakerUtils.firstName, FakerUtils.lastName,
                STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        getCustomerProfile(VALID_API_KEY, custId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(NONE_ERR_CODE))
                .body(CUSTOMER_ID_FIELD, equalTo(custId));
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(709)
    void shouldDeleteCustomerProfile() {
        deleteCustomerProfile(VALID_API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getCustomerProfile(VALID_API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(CUSTOMER_PROFILE_NOT_FOUND_ERR_CODE));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, errMsg={2}")
    @MethodSource("getCustomerProfilePagination_Valid")
    @Tag(SMOKE_TEST)
    @UserStoryId(709)
    void shouldGetCustomerProfiles_Paginated(int currentPage, int pageSize) {
        getPagination(VALID_API_KEY, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(CUSTOMERS_FIELD, hasSize(pageSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_API_KEY_01, INVALID_API_KEY_02})
    @UserStoryId(709)
    void shouldNotGetCustomerProfileWithoutProperApiKey(String key) {
        getCustomerProfile(key, customerId)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_API_KEY_01, INVALID_API_KEY_02})
    @UserStoryId({709, 1542})
    void shouldNotCreateCustomerProfileWithoutProperApiKey(String key) {
        createCustomerProfile(key, customerId, generateRandomEmail(), FakerUtils.firstName, FakerUtils.lastName,
                STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_API_KEY_01, INVALID_API_KEY_02})
    @UserStoryId(709)
    void shouldNotDeleteCustomerProfileWithoutProperApiKey(String key) {
        deleteCustomerProfile(key, customerId)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}, errMsg={2}, key={3}")
    @MethodSource("getCustomerProfilePagination_InvalidKey")
    @UserStoryId(709)
    void shouldNotGetCustomerProfiles_PaginatedWithoutProperApiKey(int currentPage, int pageSize, String key) {
        getPagination(key, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(709)
    void shouldNotGetCustomerProfileWithoutApiKey() {
        getCustomerProfileWithoutApiKey(token, customerId)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId({709, 1542})
    void shouldNotCreateCustomerProfileWithoutApiKey() {
        createCustomerProfileWithoutApiKey(token, customerId, generateRandomEmail(), FakerUtils.firstName,
                FakerUtils.lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(709)
    void shouldNotDeleteCustomerProfileWithoutApiKey() {
        deleteCustomerProfileWithoutApiKey(token, customerId)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(709)
    void shouldNotGetCustomerProfiles_PaginatedWithoutApiKey() {
        getPaginationWithoutApiKey(token, 2, 1)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }
}
