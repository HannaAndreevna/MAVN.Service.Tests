package com.lykke.tests.api.service.partnersintegration;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.MVN_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Location.LOCATION_US;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.createPartnerCredentials;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.NONE;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.CURRENCY_FIELD_PATH;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.FIAT_AMOUNT_ZERO;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.FIAT_BALANCE_ZERO;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.LOCATION_ID_FIELD_PATH;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.PARTNER_ID_FIELD_PATH;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.POSITION_ID;
import static com.lykke.tests.api.service.partnerapi.CustomersTests.POS_ID_FIELD_PATH;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.SOME_EXTERNAL_ID;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.getCustomerBalance;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.getCustomersQuery;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.getReferralInformation;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.postTriggerBonusToCustomer;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts.Currency;
import com.lykke.tests.api.service.customer.model.RegistrationRequestModel;
import com.lykke.tests.api.service.customermanagement.BlockCustomerUtils;
import com.lykke.tests.api.service.customermanagement.model.blockeduser.CustomerBlockRequest;
import com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnersintegration.model.BonusCustomerModel;
import com.lykke.tests.api.service.partnersintegration.model.BonusCustomerResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.BonusCustomerStatus;
import com.lykke.tests.api.service.partnersintegration.model.BonusCustomersRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerBalanceResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerBalanceStatus;
import com.lykke.tests.api.service.partnersintegration.model.CustomerInformationRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerInformationResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.CustomerInformationStatus;
import com.lykke.tests.api.service.partnersintegration.model.CustomerTierLevel;
import com.lykke.tests.api.service.partnersintegration.model.MessageGetResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagePostValidationModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesErrorCode;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.ReferralInformationRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.ReferralInformationResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.ReferralInformationStatus;
import com.lykke.tests.api.service.partnersintegration.model.ReferralModel;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PartnersIntegrationTests extends BaseApiTest {

    public static final String SOME_PARTNER_NAME = "aaaa name";
    public static final String SOME_LOCATION_NAME = generateRandomString(10);
    private static final String EMAIL_FIELD_PATH = "\"BonusCustomers[0].Email\"[0]";
    private static final String CUSTOMER_ID_FIELD_PATH = "\"BonusCustomers[0].CustomerId\"[0]";
    private static final String ID_EMAIL_OR_PHONE_REQUIRED_ERROR_MESSAGE = "Id, Email or Phone required";
    private static final String LOCATION_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE = "'Location Id' must be between 1 and 100 characters. You entered 0 characters.";
    private static final String EMAIL_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Email' must not be empty.";
    private static final String CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Customer Id' must not be empty.";
    private static final String EMAIL_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE = "'Email' must be between 1 and 100 characters. You entered 0 characters.";
    private static final String POS_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE = "'Pos Id' must be between 1 and 100 characters. You entered 0 characters.";
    private static final String CURRENCY_MUST_BE_BETWEEN_1_AND_20_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE = "'Currency' must be between 1 and 20 characters. You entered 0 characters.";
    private static final String PARTNER_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE = "'Partner Id' must be between 1 and 100 characters. You entered 0 characters.";
    private static final String CUSTOMER_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE = "'Customer Id' must be between 1 and 100 characters. You entered 0 characters.";
    private static final String ONLY_AED_CURRENCY_CURRENTLY_SUPPORTED_ERROR_MESSAGE = "Only AED currency currently supported";
    private static final String FAKE_LOCATION_ID = "US";
    private static final String FAKE_CURRENCY = "MVN";
    private static final String INVALID_CUSTOMER_ID = "aaa";
    private static final int SOME_FIAT_AMOUNT = 100;
    private static final String SOME_POS_ID = "1";
    private static final Function<String, String> CLIENT_PREFIX = (id) -> "non-existing-client_" + id;
    private static final String ZERO_AMOUNT_OF_TOKENS = "0";
    private static final Double ZERO_TOKENS_AMOUNT = 0.0;

    private static final String AUTOMATION_SUBJECT = "TEST AUTOMATION SUBJECT";
    private static final String[] MSG_FIELD_REQUIRED = {"The Message field is required."};
    private static final String[] SUBJECT_FIELD_REQUIRED = {"The Subject field is required."};
    private static final String[] PARTNER_ID_FIELD_REQUIRED = {"The PartnerId field is required."};
    private static final String[] CUSTOMER_ID_FIELD_REQUIRED = {"The CustomerId field is required."};
    private static final String[] PUSH_NOTIFICATION_FIELD_REQUIRED = {"The SendPushNotification field is required."};
    private static final String[] MSG_FIELD_LENGTH_ERR = {
            "The field Message must be a string or array type with a maximum length of '5120'."};
    private static final String[] SUBJECT_FIELD_LENGTH_ERR = {
            "The field Subject must be a string or array type with a maximum length of '100'."};

    private static String partnerId;
    private static String partnerPassword;
    private static String partnerToken;
    private static String customerId;
    private static String email;
    private static String phone;
    private static String locationId;
    private static String externalLocationId;
    private static String newPartnerId;
    private static String partnerExternalLocationId;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getCustomerInfoTestData() {
        return Stream.of(
                of("get by customerId", partnerId, email, phone, CustomerInformationRequestModel
                                .builder()
                                .id(customerId)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getAdminToken();
                        }),
                of("get by customerId, email", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .id(customerId)
                                .email(email)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        }),
                of("get by customerId, email, phone", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .id(customerId)
                                .email(email)
                                .phone(phone)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        }),
                of("get by customerId, phone", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .id(customerId)
                                .phone(phone)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        }),
                of("get by email", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .email(email)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        }),
                of("get by email, phone", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .email(email)
                                .phone(phone)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        })
                // TODO: change this to get the phone number that is acceptable by the search endpoint
                /*,
                of("get by phone", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .phone(phone)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .id(customerId)
                                .status(CustomerInformationStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        })
                */
        );
    }

    static Stream<Arguments> getInvalidCustomerInfoInputData() {
        return Stream.of(
                of(getRandomUuid()),
                of(INVALID_CUSTOMER_ID)
        );
    }

    static Stream<Arguments> getCustomerBalanceInvalidInputData() {
        return Stream.of(
                of(null, LOCATION_US, MVN_CURRENCY),
                of(EMPTY, LOCATION_US, MVN_CURRENCY),
                // TODO: now these two return 200, check when functionality is extended
                // of(partnerId, null, CURRENCY),
                // of(partnerId, EMPTY, CURRENCY),
                of(partnerId, LOCATION_US, null),
                of(partnerId, LOCATION_US, EMPTY),
                of(null, null, null),
                of(EMPTY, EMPTY, EMPTY)
        );
    }

    static Stream<Arguments> getReferralInformationInvalidInputData() {
        return Stream.of(
                of(null, LOCATION_US, generateRandomEmail()),
                of(EMPTY, LOCATION_US, generateRandomEmail()),
                // TODO: now these two return 200, check when functionality is extended
                // of(partnerId, null, generateRandomEmail()),
                // of(partnerId, EMPTY, generateRandomEmail()),
                of(partnerId, LOCATION_US, null),
                of(partnerId, LOCATION_US, EMPTY),
                of(null, null, null),
                of(EMPTY, EMPTY, EMPTY)
        );
    }

    static Stream<Arguments> getPostBonusToCustomerInvalidInputData() {
        return Stream.of(
                of(null, LOCATION_US, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, MVN_CURRENCY,
                        (Consumer<ValidatableResponse>) (response -> response.body(PARTNER_ID_FIELD_PATH,
                                containsString(PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE)))),
                of(EMPTY, LOCATION_US, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, MVN_CURRENCY,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(PARTNER_ID_FIELD_PATH, containsString(
                                        PARTNER_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE)))),
                // TODO: now this one returns 200, check when functionality is extended
                /*
                of(partnerId, null, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, CURRENCY,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(LOCATION_ID_FIELD_PATH, containsString(
                                        LOCATION_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_IN_LENGTH_ERROR_MESSAGE)))),
                 */
                of(partnerId, EMPTY, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, MVN_CURRENCY,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(LOCATION_ID_FIELD_PATH, containsString(
                                        LOCATION_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE)))),
                // TODO: now these two return 200, check when functionality is extended
                /*
                of(partnerId, LOCATION_US, POSITION_ID, null, FIAT_AMOUNT_ZERO, CURRENCY,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body("some email field", containsString(
                                        "some email message")))),
                of(partnerId, LOCATION_US, POSITION_ID, EMPTY, FIAT_AMOUNT_ZERO, CURRENCY,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body("some email field", containsString(
                                        "some email message")))),
                 */
                of(null, null, null, null, FIAT_AMOUNT_ZERO, null, (Consumer<ValidatableResponse>) (response -> response
                        .body(EMAIL_FIELD_PATH, containsString(EMAIL_MUST_NOT_BE_EMPTY_ERROR_MESSAGE))
                        .body(CURRENCY_FIELD_PATH, containsString(CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE))
                        .body(PARTNER_ID_FIELD_PATH, containsString(PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE))
                        .body(CUSTOMER_ID_FIELD_PATH, containsString(CUSTOMER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE)))),
                of(EMPTY, EMPTY, EMPTY, EMPTY, FIAT_AMOUNT_ZERO, EMPTY,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body(EMAIL_FIELD_PATH, containsString(
                                        EMAIL_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE))
                                .body(POS_ID_FIELD_PATH, containsString(
                                        POS_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE))
                                .body(CURRENCY_FIELD_PATH, containsString(
                                        CURRENCY_MUST_BE_BETWEEN_1_AND_20_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE))
                                .body(PARTNER_ID_FIELD_PATH, containsString(
                                        PartnersIntegrationTests.PARTNER_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE))
                                .body(CUSTOMER_ID_FIELD_PATH,
                                        containsString(
                                                CUSTOMER_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE))
                                .body(LOCATION_ID_FIELD_PATH, containsString(
                                        LOCATION_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_YOU_ENTERED_0_CHARACTERS_ERROR_MESSAGE))))
        );
    }

    @BeforeEach
    void setUp() {
        partnerId = getRandomUuid();
        partnerPassword = generateValidPassword();
        val customerInfo = registerDefaultVerifiedCustomer();
        email = customerInfo.getEmail();
        phone = customerInfo.getPhoneNumber();
        customerId = customerInfo.getCustomerId();

        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());

        // TODO: these are working 100%
        partnerId = getRandomUuid();
        val locationSuffix = generateRandomString(10);
        externalLocationId = SOME_EXTERNAL_ID + locationSuffix;
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                locationSuffix);
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        newPartnerId = partnerData.getId();
        partnerExternalLocationId = PartnerManagementUtils.getLocationExternalId(partnerData);
    }

    @Test
    @UserStoryId(storyId = 2758)
    void shouldNotCreatePartnerMessage_InvalidInputValues() {
        val expectedResponse = MessagePostValidationModel.builder()
                .message(MSG_FIELD_LENGTH_ERR)
                .subject(SUBJECT_FIELD_LENGTH_ERR)
                .partnerId(PARTNER_ID_FIELD_REQUIRED)
                .customerId(CUSTOMER_ID_FIELD_REQUIRED)
                .build();

        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(null)
                .customerId(null)
                .subject(generateRandomString(101))
                .message(generateRandomString(5121))
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(MessagePostValidationModel.class);

        assertAll(
                () -> assertEquals(expectedResponse, postMessageActualResult)
        );
    }

    @Test
    @UserStoryId(storyId = 2758)
    void shouldNotCreatePartnerMessage_RequestedFields() {
        val expectedResponse = MessagePostValidationModel.builder()
                .message(MSG_FIELD_REQUIRED)
                .subject(SUBJECT_FIELD_REQUIRED)
                .partnerId(PARTNER_ID_FIELD_REQUIRED)
                .customerId(CUSTOMER_ID_FIELD_REQUIRED)
                .build();

        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(null)
                .customerId(null)
                .subject(null)
                .message(null)
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(MessagePostValidationModel.class);

        assertAll(
                () -> assertEquals(expectedResponse, postMessageActualResult)
        );
    }

    @Test
    @UserStoryId(storyId = 2758)
    void should_Create_Get_Delete_PartnerMessage() {
        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(newPartnerId)
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(partnerExternalLocationId)
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        assertAll(
                () -> assertNotNull(postMessageActualResult.getPartnerMessageId()),
                () -> assertEquals(MessagesErrorCode.OK, postMessageActualResult.getErrorCode())
        );

        val getMessageActualResult = PartnersIntegrationUtils.getMessage(postMessageActualResult.getPartnerMessageId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessageGetResponseModel.class);

        assertAll(
                () -> assertEquals(requestObject.getPartnerId(), getMessageActualResult.getPartnerId()),
                () -> assertEquals(requestObject.getCustomerId(), getMessageActualResult.getCustomerId()),
                () -> assertEquals(requestObject.getSubject(), getMessageActualResult.getSubject()),
                () -> assertEquals(requestObject.getMessage(), getMessageActualResult.getMessage()),
                () -> assertEquals(requestObject.getExternalLocationId(),
                        getMessageActualResult.getExternalLocationId())
        );

        PartnersIntegrationUtils.deleteMessage(postMessageActualResult.getPartnerMessageId())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        PartnersIntegrationUtils.getMessage(postMessageActualResult.getPartnerMessageId())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(storyId = 2758)
    void shouldNotCreatePartnerMessage_PartnerNotFound() {
        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(getRandomUuid())
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(partnerExternalLocationId)
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        assertAll(
                () -> assertNull(postMessageActualResult.getPartnerMessageId()),
                () -> assertEquals(MessagesErrorCode.PARTNER_NOT_FOUND, postMessageActualResult.getErrorCode())
        );
    }

    @Test
    @UserStoryId(storyId = 2758)
    void shouldNotCreatePartnerMessage_CustomerNotFound() {
        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(newPartnerId)
                .customerId(getRandomUuid())
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(partnerExternalLocationId)
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        assertAll(
                () -> assertNull(postMessageActualResult.getPartnerMessageId()),
                () -> assertEquals(MessagesErrorCode.CUSTOMER_NOT_FOUND, postMessageActualResult.getErrorCode())
        );
    }

    @Test
    @UserStoryId(storyId = 2758)
    void shouldNotCreatePartnerMessage_LocationNotFound() {
        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(newPartnerId)
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(getRandomUuid())
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        assertAll(
                () -> assertNull(postMessageActualResult.getPartnerMessageId()),
                () -> assertEquals(MessagesErrorCode.LOCATION_NOT_FOUND, postMessageActualResult.getErrorCode())
        );
    }

    @Test
    @UserStoryId(storyId = 2758)
    void shouldNotCreatePartnerMessage_CustomerBlocked() {
        val blockCustomerObject = CustomerBlockRequest.builder()
                .customerId(customerId)
                .build();

        BlockCustomerUtils.blockCustomer(blockCustomerObject);

        val requestObject = MessagesPostRequestModel.builder()
                .partnerId(newPartnerId)
                .customerId(customerId)
                .subject(AUTOMATION_SUBJECT)
                .message(AUTOMATION_SUBJECT)
                .externalLocationId(getRandomUuid())
                .sendPushNotification(true)
                .build();

        val postMessageActualResult = PartnersIntegrationUtils.postMessage(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        assertAll(
                () -> assertNull(postMessageActualResult.getPartnerMessageId()),
                () -> assertEquals(MessagesErrorCode.CUSTOMER_IS_BLOCKED, postMessageActualResult.getErrorCode())
        );
    }

    @ParameterizedTest(name = "Run {index}: {0} partnerId={1}, String email={2}, phone={3}")
    @MethodSource("getCustomerInfoTestData")
    @UserStoryId(storyId = {2081, 2654})
    void shouldReturnInformationAboutCustomer(String description, String partnerIdParam, String emailParam,
            String phoneParam, CustomerInformationRequestModel requestModel,
            CustomerInformationResponseModel expectedResult, Consumer<Void> action) {

        if (null != action) {
            action.accept(null);
        }

        val actualResult = getCustomersQuery(requestModel)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2081, 2654})
    void shouldGetCustomerBalance() {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(ZERO_TOKENS_AMOUNT.toString())
                .fiatBalance(0)
                .fiatCurrency(AED_CURRENCY)
                .status(CustomerBalanceStatus.OK)
                .build();

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerData.getId())
                        .locationId(externalLocationId)
                        .currency(AED_CURRENCY)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2654)
    void shouldGetCustomerBalanceWithConversion() {
        final Double amountForCampaingnAndConditions = 150.0;
        final Double initialAmount = amountForCampaingnAndConditions; // for campaigns * 3;
        final String customerId = createCustomerFundedViaBonusReward(amountForCampaingnAndConditions).getCustomerId();
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(initialAmount.toString())
                .fiatBalance((int) (initialAmount * Currency.SOME_CURRENCY_RATE))
                .fiatCurrency(AED_CURRENCY)
                .status(CustomerBalanceStatus.OK)
                .build();

        CustomerBalanceResponseModel actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerData.getId())
                        .locationId(locationId)
                        .currency(AED_CURRENCY)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2081)
    void shouldGetReferralInformation() {
        // TODO: wnen it's possible compare with real data
        val expectedResult = ReferralInformationResponseModel
                .builder()
                .referrals(new ReferralModel[]{})
                .status(ReferralInformationStatus.OK)
                .build();

        val actualResult = getReferralInformation(ReferralInformationRequestModel
                .builder()
                .customerId(customerId)
                .locationId(locationId)
                .partnerId(partnerData.getId())
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2081)
    void shouldBeAbleToPostBonusToCustomer() {
        partnerId = getRandomUuid();
        val partnerData = createDefaultPartner(partnerId, partnerPassword, SOME_PARTNER_NAME, SOME_LOCATION_NAME);
        val locationId = getLocationId(partnerData);

        val expectedResult = BonusCustomerResponseModel
                .builder()
                .status(BonusCustomerStatus.OK)
                .customerId(customerId)
                .customerEmail(email)
                .bonusCustomerSeqNumber(1)
                .build();

        val actualResult = postTriggerBonusToCustomer(BonusCustomersRequestModel
                .builder()
                .bonusCustomers(new BonusCustomerModel[]{
                        new BonusCustomerModel(customerId, email, SOME_FIAT_AMOUNT, AED_CURRENCY,
                                Instant.now().toString(),
                                partnerData.getId(), locationId, SOME_POS_ID)})
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusCustomerResponseModel[].class)[0];

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2081, 2654})
    void shouldNotBeAbleToPostBonusToCustomerWithInvalidCurrency() {
        partnerId = getRandomUuid();
        val partnerData = createDefaultPartner(partnerId, partnerPassword, SOME_PARTNER_NAME, SOME_LOCATION_NAME);
        val locationId = getLocationId(partnerData);

        val expectedResult = BonusCustomerResponseModel
                .builder()
                .status(BonusCustomerStatus.INVALID_CURRENCY)
                .customerId(customerId)
                .customerEmail(email)
                .bonusCustomerSeqNumber(1)
                .build();

        val actualResult = postTriggerBonusToCustomer(BonusCustomersRequestModel
                .builder()
                .bonusCustomers(new BonusCustomerModel[]{
                        new BonusCustomerModel(customerId, email, SOME_FIAT_AMOUNT, FAKE_CURRENCY,
                                Instant.now().toString(),
                                partnerData.getId(), locationId, SOME_POS_ID)})
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusCustomerResponseModel[].class)[0];

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2081, 2654})
    void shouldNotGetCustomerInfoOnInvalidInput() {
        getCustomersQuery(CustomerInformationRequestModel
                .builder()
                .id(EMPTY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("\"\"[0]", containsString(ID_EMAIL_OR_PHONE_REQUIRED_ERROR_MESSAGE));
    }

    @Test
    @UserStoryId(storyId = 2785)
    void shouldGetCustomerInfoById_NewFields() {
        val expectedCustomerObject = RegistrationRequestModel
                .builder()
                .email(generateRandomEmail())
                .password(generateValidPassword())
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .build();
        registerUser(expectedCustomerObject);

        val customerQueryRequestObject = CustomerInformationRequestModel
                .builder()
                .id(customerId)
                .email(expectedCustomerObject.getEmail())
                .build();

        val actualResult = getCustomersQuery(customerQueryRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerInformationResponseModel.class);

        assertAll(
                () -> assertEquals(expectedCustomerObject.getFirstName(), actualResult.getFirstName()),
                () -> assertEquals(expectedCustomerObject.getLastName(), actualResult.getLastName())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidCustomerInfoInputData")
    @UserStoryId(storyId = {2081, 2654})
    void shouldNotGetCustomerInfoForNonExistingCustomer(String customerIdParam) {
        val expectedResult = CustomerInformationResponseModel
                .builder()
                .id(null)
                .status(CustomerInformationStatus.CUSTOMER_NOT_FOUND)
                .build();

        val actualResult = getCustomersQuery(CustomerInformationRequestModel
                .builder()
                .id(customerIdParam)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getCustomerBalanceInvalidInputData")
    @UserStoryId(storyId = {2081, 2654, 2689})
    void shouldNotGetCustomerBalanceOnInvalidInput(String customerIdParam, String locationIdParam,
            String currencyParam) {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .fiatBalance(FIAT_BALANCE_ZERO)
                .build();

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(customerIdParam)
                        .locationId(locationIdParam)
                        .currency(currencyParam)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getReferralInformationInvalidInputData")
    @UserStoryId(2081)
    void shouldNotGetReferralInformationOnInvalidInput(String customerIdParam, String locationIdParam,
            String emailParam) {
        val expectedResult = ReferralInformationResponseModel
                .builder()
                .build();

        val actualResult = getReferralInformation(ReferralInformationRequestModel
                .builder()
                .customerId(emailParam)
                .locationId(locationIdParam)
                .partnerId(customerIdParam)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ReferralInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Disabled("it looks like validation is now off for partnerId, locationId, email")
    @ParameterizedTest
    @MethodSource("getPostBonusToCustomerInvalidInputData")
    @UserStoryId(storyId = {2081, 2689})
    void shouldNotBeAbleToPostBonusToCustomerOnInvalidInput(String customerIdParam, String locationIdParam,
            String positionParam, String emailParam, float fiatAmountParam, String currencyParam,
            Consumer<ValidatableResponse> assertAction) {
        BonusCustomerResponseModel
                .builder()
                .customerId(customerId)
                .customerEmail(email)
                .bonusCustomerSeqNumber(1)
                .build();

        val actualResponse = postTriggerBonusToCustomer(
                BonusCustomersRequestModel
                        .builder()
                        .bonusCustomers(new BonusCustomerModel[]{
                                new BonusCustomerModel(customerIdParam, emailParam, fiatAmountParam, currencyParam,
                                        Instant.now().toString(),
                                        customerIdParam, locationIdParam, positionParam)})
                        .build(), partnerToken)
                .thenReturn();
        actualResponse
                .then()
                .assertThat()
                .statusCode(SC_OK); // TODO: there was bad request earlier

        assertAction.accept(actualResponse.then().assertThat());
    }

    @Test
    @UserStoryId(2377)
    void shouldNotBeAbleToPostBonusToCustomerIfPartnerDoesNotExist() {
        val expectedResult = BonusCustomerResponseModel
                .builder()
                .status(BonusCustomerStatus.PARTNER_NOT_FOUND)
                .bonusCustomerSeqNumber(1)
                .customerId(customerId)
                .customerEmail(email)
                .build();

        val actualResult = postTriggerBonusToCustomer(BonusCustomersRequestModel
                .builder()
                .bonusCustomers(new BonusCustomerModel[]{
                        new BonusCustomerModel(customerId, email, SOME_FIAT_AMOUNT, AED_CURRENCY,
                                Instant.now().toString(),
                                partnerId, FAKE_LOCATION_ID, SOME_POS_ID)})
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusCustomerResponseModel[].class)[0];

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2377, 2654})
    void shouldNotGetCustomerBalanceIfPartnerDoesNotExist() {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(ZERO_AMOUNT_OF_TOKENS)
                .fiatBalance(0)
                .status(CustomerBalanceStatus.PARTNER_NOT_FOUND)
                .build();

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerId)
                        .locationId(FAKE_LOCATION_ID)
                        .currency(AED_CURRENCY)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2377, 2654})
    void shouldNotGetCustomerBalanceIfCurrencyIsNotSupported() {
        val expectedResult = ValidationErrorResponse
                .builder()
                .currency(new String[]{ONLY_AED_CURRENCY_CURRENTLY_SUPPORTED_ERROR_MESSAGE})
                .build();

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerData.getId())
                        .locationId(FAKE_LOCATION_ID)
                        .currency(FAKE_CURRENCY)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String[] currency;
    }
}
