package com.lykke.tests.api.service.customerprofile;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomHash;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomPhone;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.API_KEY;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.createCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.createCustomerProfileWithPhoneNumber;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.deleteCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomerProfileByEmail;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomerProfileByPhone;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getCustomersCount;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.getPagination;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.postCustomerProfileKey;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.setCustomerEmailAsVerified;
import static com.lykke.tests.api.service.customerprofile.CustomerProfileUtils.updateCustomerProfile;
import static com.lykke.tests.api.service.customerprofile.model.CustomerProfileErrorCode.CUSTOMER_PROFILE_DOES_NOT_EXIST;
import static com.lykke.tests.api.service.customerprofile.model.LoginProvider.GOOGLE;
import static com.lykke.tests.api.service.customerprofile.model.LoginProvider.STANDARD;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.RegistrationRequestModel;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileRequestModel;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileResponse;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileUpdateRequestModel;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CustomerProfileTests extends BaseApiTest {

    private static final int INVALID_KEY_LENGTH_01 = 1;
    private static final int INVALID_KEY_LENGTH_02 = 5;
    private static final int INVALID_KEY_LENGTH_03 = 31;
    private static final int INVALID_KEY_LENGTH_04 = 33;
    private static final int INVALID_KEY_LENGTH_05 = 1000000;
    private static final int TOO_LONG_INT = 2_147_483_647 + 1;
    private static final String INVALID_KEY = "a2V5a2V5a2V5a2V5a2V5a2V5a2V5a2V5a2V5a2V5MTI1";
    private static final String EMPTY_KEY = EMPTY;
    private static final String ERROR_MESSAGE_PATH = "ErrorMessage";
    private static final String ERROR_CODE_PATH = "ErrorCode";
    private static final String ERROR_MESSAGE_KEY_IS_ALREADY_SET = "Key is already set.";
    private static final String ERROR_MESSAGE_THE_KEY_FIELD_IS_REQUIRED = "The Key field is required.";
    private static final String CUSTOMER_PROFILE_NOT_FOUND_ERR_CODE = "CustomerProfileDoesNotExist";
    private static final String CUSTOMER_PROFILE_ALREADY_EXISTS_ERR_CODE = "CustomerProfileAlreadyExists";
    private static final String NONE_ERR_CODE = "None";
    private static final String CUSTOMER_ID_FIELD = "Profile.CustomerId";
    private static final String CURRENT_PAGE_FIELD = "ModelErrors.CurrentPage";
    private static final String PAGE_SIZE_FIELD = "ModelErrors.PageSize";
    private static final String EMAIL_FIELD = "Profile.Email";
    private static final String CUSTOMERS_FIELD = "Customers";
    private static final String PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG = "Current page can't be less than 1 or greater than 2147483647";
    private static final String PAGINATION_LOWER_BOUND_ERROR_MESSAGE = "Page Size can't be less than 1";
    private static final String PAGINATION_UPPER_BOUND_ERROR_MESSAGE = "Page Size cannot exceed more then 1000";
    private static final String NAME_FIELD = "name";
    private static final String TOTAL_COUNT = "TotalCount";
    private static final String IS_EMAIL_VERIFIED_FIELD = "Profile.IsEmailVerified";
    private static final String CUSTOMER_PROFILE_EMAIL_IS_ALREADY_VERIFIED_ERR_MSG = "CustomerProfileEmailAlreadyVerified";
    private static final String CUSTOMER_PROFILE_DOES_NOT_EXIST_ERR_MSG = "CustomerProfileDoesNotExist";
    private static final String ERROR_MESSAGE_FIELD = "ErrorMessage";
    private static final String LOGIN_PROVIDER_FIELD = "Profile.LoginProviders[0]";
    private static final String TIER_ID_FIELD = "Profile.TierId";
    private static final String THE_FIELD_PHONE_NUMBER_MUST_BE_A_STRING_OR_ARRAY_TYPE_WITH_A_MAXIMUM_LENGTH_OF_50 = "The field PhoneNumber must be a string or array type with a maximum length of '15'.";
    private static final String INVALID_PHONE_NUMBER_01 = "aaa" + FakerUtils.phoneNumber;
    private static final String INVALID_PHONE_NUMBER_02 = "111" + generateRandomHash().substring(0, 20);
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String country;

    private static String encryptKey(String key) {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }

    private static Stream<Arguments> getCustomerProfilePagination_Valid() {
        return Stream.of(
                of(1, 2),
                of(2, 1)
        );
    }

    private static Stream<Arguments> getCustomerProfilePagination_Invalid() {
        return Stream.of(
                of(-1, 2, PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG),
                of(-2, 1, PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG),
                of(TOO_LONG_INT, 1, PAGINATION_INVALID_CURRENT_PAGE_ERR_MSG)
        );
    }

    private static Stream<Arguments> getCustomerProfilePagination_InvalidPageSize() {
        return Stream.of(
                of(2, -1, PAGINATION_LOWER_BOUND_ERROR_MESSAGE),
                of(1, 1001, PAGINATION_UPPER_BOUND_ERROR_MESSAGE)
        );
    }

    static Stream<Arguments> getInvalidPhoneNumbers() {
        return Stream.of(
                of(INVALID_PHONE_NUMBER_01),
                of(INVALID_PHONE_NUMBER_02)
        );
    }

    @BeforeEach
    void methodSetup() {
        firstName = FakerUtils.firstName;
        lastName = FakerUtils.lastName;
        phone = FakerUtils.phoneNumber;
        email = generateRandomEmail();
        country = FakerUtils.country;
    }

    @Test
    @UserStoryId(754)
    void shouldNotPostTheSameValidEncryptionKey() {
        postCustomerProfileKey(registerNewUserAndGetItsToken(), getEncryptedKey())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_PATH, equalTo(ERROR_MESSAGE_KEY_IS_ALREADY_SET));
    }

    @Test
    @UserStoryId(754)
    void shouldNotPostInvalidEncryptionKey() {
        postCustomerProfileKey(getAdminToken(), INVALID_KEY)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_PATH, equalTo(ERROR_MESSAGE_KEY_IS_ALREADY_SET));
    }

    @Test
    @UserStoryId(754)
    void shouldNotPostEmptyEncryptionKey() {
        postCustomerProfileKey(getAdminToken(), EMPTY_KEY)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_PATH, equalTo(ERROR_MESSAGE_THE_KEY_FIELD_IS_REQUIRED));
    }

    @ParameterizedTest
    @ValueSource(ints = {INVALID_KEY_LENGTH_01, INVALID_KEY_LENGTH_02, INVALID_KEY_LENGTH_03, INVALID_KEY_LENGTH_04,
            INVALID_KEY_LENGTH_05})
    @UserStoryId(754)
    void shouldNotPostEncryptionKeyOfWrongLength(int keyLength) {
        postCustomerProfileKey(getAdminToken(), getEncryptedKey(keyLength))
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_PATH, equalTo(ERROR_MESSAGE_KEY_IS_ALREADY_SET));
    }

    @Test
    @UserStoryId(storyId = 2292)
    void shouldPopulateTierOnRegister() {
        val customerId = registerCustomer();

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TIER_ID_FIELD, notNullValue());
    }

    @Test
    @UserStoryId(storyId = {635, 1542, 2623})
    void shouldCreateUserProfileAndDelete() {
        val customerId = getRandomUuid();
        createCustomerProfile(API_KEY, customerId, generateRandomEmail(), FakerUtils.firstName, FakerUtils.lastName,
                STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(NONE_ERR_CODE))
                .body(CUSTOMER_ID_FIELD, equalTo(customerId));

        deleteCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(CUSTOMER_PROFILE_NOT_FOUND_ERR_CODE));
    }

    @Test
    @UserStoryId(storyId = {635, 1542, 2623})
    void shouldCreateUserProfile_AfterDeletingExistingOne() {
        var customerId = getRandomUuid();
        createCustomerProfile(API_KEY, customerId, email, firstName, lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(NONE_ERR_CODE))
                .body(CUSTOMER_ID_FIELD, equalTo(customerId));

        deleteCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        customerId = getRandomUuid();

        createCustomerProfile(API_KEY, customerId, email, firstName, lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(NONE_ERR_CODE))
                .body(CUSTOMER_ID_FIELD, equalTo(customerId))
                .body(EMAIL_FIELD, equalTo(email));
    }

    @Test
    @UserStoryId(storyId = {635, 1542, 2623})
    void shouldNotCreateCustomerProfile_SameCustomer_DifferentEmail() {
        val customerId = getRandomUuid();
        createCustomerProfile(API_KEY, customerId, email, firstName, lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        email = generateRandomEmail();

        createCustomerProfile(API_KEY, customerId, email, firstName, lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(CUSTOMER_PROFILE_ALREADY_EXISTS_ERR_CODE));
    }

    @Test
    @UserStoryId(storyId = {635, 2623})
    void shouldNotCreateCustomerProfile_AlreadyExists() {
        val customerId = getRandomUuid();
        createCustomerProfile(API_KEY, customerId, email, firstName, lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        createCustomerProfile(API_KEY, customerId, email, firstName, lastName, STANDARD)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(CUSTOMER_PROFILE_ALREADY_EXISTS_ERR_CODE));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getCustomerProfilePagination_Valid")
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 635)
    void shouldGetCustomerProfiles_Paginated(int currentPage, int pageSize) {
        getPagination(API_KEY, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(TOTAL_COUNT, equalTo(getCustomersCount(API_KEY)))
                .body(CUSTOMERS_FIELD, hasSize(pageSize));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getCustomerProfilePagination_Invalid")
    @UserStoryId(storyId = 635)
    void shouldGetNotCustomerProfiles_Paginated_Invalid(int currentPage, int pageSize, String errMsg) {
        getPagination(API_KEY, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(CURRENT_PAGE_FIELD + "[0]", equalTo(errMsg));
    }

    @ParameterizedTest(name = "Run {index}: currentPage={0}, pageSize={1}")
    @MethodSource("getCustomerProfilePagination_InvalidPageSize")
    @UserStoryId(storyId = 635)
    void shouldGetNotCustomerProfiles_Paginated_InvalidPageSize(int currentPage, int pageSize, String errMsg) {
        getPagination(API_KEY, currentPage, pageSize)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_MESSAGE_FIELD, equalTo(errMsg))
                .body(PAGE_SIZE_FIELD + "[0]", equalTo(errMsg));
    }

    @Disabled("Test fails because the email verification endpoind is moved to another service or workflow is changed")
    @Test
    @UserStoryId(storyId = 655)
    void shouldSetCustomerEmailIsValid() {
        var customerId = registerCustomer();

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(NONE_ERR_CODE))
                .body(IS_EMAIL_VERIFIED_FIELD, equalTo(false));

        setCustomerEmailAsVerified(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        getCustomerProfile(API_KEY, customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_PATH, equalTo(NONE_ERR_CODE))
                .body(IS_EMAIL_VERIFIED_FIELD, equalTo(true));
    }

    @Disabled("Test fails because the email verification endpoind is moved to another service or workflow is changed")
    @Test
    @UserStoryId(storyId = 655)
    void shouldNotSetCustomerEmailIsValidTwice() {
        var customerId = registerCustomer();

        setCustomerEmailAsVerified(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(NONE_ERR_CODE));

        setCustomerEmailAsVerified(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(CUSTOMER_PROFILE_EMAIL_IS_ALREADY_VERIFIED_ERR_MSG));
    }

    @Disabled("Test fails because the email verification endpoind is moved to another service or workflow is changed")
    @Test
    @UserStoryId(storyId = 655)
    void shouldNotSetCustomerEmailIsValidWhenCustomerIdIsNotValid() {
        var customerId = UUID.randomUUID().toString();

        setCustomerEmailAsVerified(customerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(containsString(CUSTOMER_PROFILE_DOES_NOT_EXIST_ERR_MSG));
    }

    @Test
    @UserStoryId(storyId = {1569, 3770})
    void shouldCreateCustomerProfileWithGoogleLoginProvider() {
        val customerProfileRequest = CustomerProfileRequestModel
                .builder()
                .customerId(getRandomUuid())
                .email(generateRandomEmail())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .loginProvider(GOOGLE)
                .build();

        createCustomerProfile(customerProfileRequest);

        getCustomerProfile(API_KEY, customerProfileRequest.getCustomerId())
                .then()
                /*
                {
                    "Profile": null,
                    "ErrorCode": "CustomerProfileDoesNotExist"
                }
                */
                .assertThat()
                .statusCode(SC_OK)
                .body(LOGIN_PROVIDER_FIELD, equalTo(GOOGLE.getCode()));
    }

    @Test
    @UserStoryId(1959)
    void shouldNotRegisterCustomerWithTooLongPhoneNumber() {
        val customerWithPhoneId = registerCustomer();
        updateCustomerProfile(
                CustomerProfileUpdateRequestModel
                        .builder()
                        .customerId(customerWithPhoneId)
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneNumber(generateRandomPhone(51))
                        .countryPhoneCodeId(1)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("ErrorMessage",
                        equalTo(THE_FIELD_PHONE_NUMBER_MUST_BE_A_STRING_OR_ARRAY_TYPE_WITH_A_MAXIMUM_LENGTH_OF_50));
    }

    @Test
    @UserStoryId(2134)
    void shouldReturnCustomerByPhone() {
        val customerEmail = generateRandomEmail();
        createCustomerProfileWithPhoneNumber(customerEmail, phone);

        val expectedResult = getCustomerProfileByEmail(customerEmail)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerProfileResponse.class);

        val actualResult = getCustomerProfileByPhone(expectedResult.getProfile().getPhoneNumber())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerProfileResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPhoneNumbers")
    @UserStoryId(2134)
    void shouldNotReturnCustomerByPhoneOnProcessedInvalidInput(String phoneNumber) {
        val customerEmail = generateRandomEmail();
        createCustomerProfileWithPhoneNumber(customerEmail, phoneNumber);

        val expectedResult = getCustomerProfileByEmail(customerEmail)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerProfileResponse.class);

        val actualResult = getCustomerProfileByPhone(expectedResult.getProfile().getPhoneNumber())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerProfileResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {EMPTY})
    @UserStoryId(2134)
    void shouldNotReturnCustomerByPhoneOnEmptyInput(String phoneNumber) {
        val actualResult = getCustomerProfileByPhone(phoneNumber)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerProfileResponse.class);

        assertAll(
                () -> assertNull(actualResult.getProfile()),
                () -> assertEquals(CUSTOMER_PROFILE_DOES_NOT_EXIST, actualResult.getErrorCode())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidPhoneNumbers")
    @UserStoryId(2134)
    void shouldNotReturnCustomerByPhoneOnInvalidInput(String phoneNumber) {
        // TODO: if some input validation is applied, change expectations to 400 or 404
        getCustomerProfileByPhone(phoneNumber)
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }

    private String registerNewUserAndGetItsToken() {
        var user = new RegistrationRequestModel();
        registerUser();
        return getUserToken(user);
    }

    private String getEncryptedKey() {
        val key = generateRandomString(32);
        return Base64.getEncoder().encodeToString(key.getBytes());
    }

    private String getEncryptedKey(int length) {
        val key = generateRandomString(length);
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
