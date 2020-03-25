package com.lykke.tests.api.service.partnerapi;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.AED_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.MVN_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.FAKE_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Currency.USD_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.Location.LOCATION_FAKE;
import static com.lykke.tests.api.common.CommonConsts.Location.LOCATION_US;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.credentials.CredentialsUtils.createPartnerCredentials;
import static com.lykke.tests.api.service.credentials.model.CredentialsError.NONE;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getCustomerBalance;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getCustomerBalance_Deprecated;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getCustomerInfo;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getReferralInformation;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.postTriggerBonusToCustomer;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.SOME_EXTERNAL_ID;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerInfo;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomerModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomerResponseModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomersRequestModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerBalanceResponseModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerBalanceStatus;
import com.lykke.tests.api.service.partnerapi.model.CustomerInformationRequestModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerInformationResponseModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerTierLevel;
import com.lykke.tests.api.service.partnerapi.model.ReferralInfo;
import com.lykke.tests.api.service.partnerapi.model.ReferralInformationRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ReferralInformationResponseModel;
import com.lykke.tests.api.service.partnerapi.model.ReferralInformationStatus;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
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

public class CustomersTests extends BaseApiTest {

    public static final String MUST_HAVE_VALUE_ERROR_MESSAGE = "At least one of three (customer id, email, phone) parameters must have value";
    public static final String PARTNER_ID_IS_REQUIRED_ERROR_MESSAGE = "Partner id is required";
    public static final String PARTNER_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Partner Id' must not be empty.";
    public static final String CURRENCY_ID_IS_REQUIRED_ERROR_MESSAGE = "Currency id is required";
    public static final String CURRENCY_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Currency' must not be empty.";
    public static final String LOCATION_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_IN_LENGTH_ERROR_MESSAGE = "Location id must be between 1 and 100 characters in length";
    public static final String CUSTOMER_ID_OR_EMAIL_IS_REQUIRED_ERROR_MESSAGE = "Customer id or email is required";
    public static final String POS_ID_MUST_BE_BETWEEN_1_AND_100_CHARACTERS_IN_LENGTH_ERROR_MESSAGE = "POS id must be between 1 and 100 characters in length";
    public static final String CURRENCY_FIELD_PATH = "\"BonusCustomers[0].Currency\"[0]";
    public static final String PARTNER_ID_FIELD_PATH = "\"bonusCustomers[0].partnerId\"[0]";
    public static final String LOCATION_ID_FIELD_PATH = "\"BonusCustomers[0].LocationId\"[0]";
    public static final String POS_ID_FIELD_PATH = "\"BonusCustomers[0].PosId\"[0]";
    public static final String BONUS_CUSTOMERS_PATH = "\"BonusCustomers[0]\"[0]";
    public static final String POSITION_ID = "1";
    public static final Double DEFAULT_FIAT_AMOUNT = 100.0;
    public static final Double FIAT_AMOUNT_ZERO = 0.0;
    public static final int TOKENS_ZERO = 0;
    public static final int FIAT_BALANCE_ZERO = 0;
    private static final String ONLY_AED_CURRENCY_CURRENTLY_SUPPORTED_ERROR_MESSAGE = "Only AED is allowed as currency at the moment";
    private static final Function<String, String> CLIENT_PREFIX = (id) -> "non-existing-client_" + id;
    private static String partnerId;
    private static String partnerPassword;
    private static String partnerToken;
    private static String customerId;
    private static String email;
    private static String phone;
    private static String locationId;
    private static String externalLocationId;
    private static CustomerInfo customerInfo;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getCustomerInfoTestData() {
        // TODO: change this to get the phone number that is acceptable by the search endpooint
        // phone = "+1 " + phone;
        return Stream.of(
                of("get by customerId", partnerId, email, phone, CustomerInformationRequestModel
                                .builder()
                                .customerId(customerId)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getAdminToken();
                        }),
                of("get by customerId, email", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .customerId(customerId)
                                .email(email)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        }),
                of("get by customerId, email, phone", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .customerId(customerId)
                                .email(email)
                                .phone(phone)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
                                .tierLevel(CustomerTierLevel.BLACK)
                                .build(), (Consumer<Void>) (a) -> {
                            partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
                        }),
                of("get by customerId, phone", partnerId, email, phone,
                        CustomerInformationRequestModel
                                .builder()
                                .customerId(customerId)
                                .phone(phone)
                                .build(),
                        CustomerInformationResponseModel
                                .builder()
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
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
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
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
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
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
                                .customerId(customerId)
                                .status(CustomerBalanceStatus.OK)
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
                of("aaa")
        );
    }

    static Stream<Arguments> getCustomerBalanceInvalidInputData() {
        return Stream.of(
                // TODO: now these two return 200, check when functionality is extended
                // of(partnerId, null, MVN_CURRENCY, SC_BAD_REQUEST, CustomerBalanceResponseModel
                //                        .builder()
                //                        .tokens(TOKENS_ZERO)
                //                        .fiatBalance(FIAT_BALANCE_ZERO)
                //                        .build()),
                // of(partnerId, EMPTY, MVN_CURRENCY, SC_BAD_REQUEST, CustomerBalanceResponseModel
                //                        .builder()
                //                        .tokens(TOKENS_ZERO)
                //                        .fiatBalance(FIAT_BALANCE_ZERO)
                //                        .build()),
                of(partnerId, LOCATION_US, null, SC_BAD_REQUEST, CustomerBalanceResponseModel
                        .builder()
                        .tokens(TOKENS_ZERO)
                        .fiatBalance(FIAT_BALANCE_ZERO)
                        .build()),
                of(partnerId, LOCATION_US, EMPTY, SC_BAD_REQUEST, CustomerBalanceResponseModel
                        .builder()
                        .tokens(TOKENS_ZERO)
                        .fiatBalance(FIAT_BALANCE_ZERO)
                        .build()),
                of(null, null, null, SC_BAD_REQUEST, CustomerBalanceResponseModel
                        .builder()
                        .tokens(TOKENS_ZERO)
                        .fiatBalance(FIAT_BALANCE_ZERO)
                        .build()),
                of(EMPTY, EMPTY, EMPTY, SC_BAD_REQUEST, CustomerBalanceResponseModel
                        .builder()
                        .tokens(TOKENS_ZERO)
                        .fiatBalance(FIAT_BALANCE_ZERO)
                        .build())
        );
    }

    static Stream<Arguments> getCustomerBalanceInvalidInputData2() {
        return Stream.of(
                of(null, LOCATION_US, USD_CURRENCY, SC_OK, CustomerBalanceResponseModel
                        .builder()
                        .tokens(TOKENS_ZERO)
                        .fiatBalance(FIAT_BALANCE_ZERO)
                        .fiatCurrency(USD_CURRENCY)
                        .status(CustomerBalanceStatus.OK)
                        .build()),
                of(EMPTY, LOCATION_US, USD_CURRENCY, SC_OK, CustomerBalanceResponseModel
                        .builder()
                        .tokens(TOKENS_ZERO)
                        .fiatBalance(FIAT_BALANCE_ZERO)
                        .fiatCurrency(USD_CURRENCY)
                        .status(CustomerBalanceStatus.OK)
                        .build())
        );
    }

    static Stream<Arguments> getReferralInformationInvalidInputData() {
        return Stream.of(
                of(null, LOCATION_FAKE, generateRandomEmail(), SC_OK, ReferralInformationResponseModel
                        .builder()
                        .referrals(new ReferralInfo[]{})
                        .status(ReferralInformationStatus.OK)
                        .build()),
                of(EMPTY, LOCATION_FAKE, generateRandomEmail(), SC_OK, ReferralInformationResponseModel
                        .builder()
                        .referrals(new ReferralInfo[]{})
                        .status(ReferralInformationStatus.OK)
                        .build()),
                // TODO: now these two return 200, check when functionality is extended
                // of(partnerId, null, generateRandomEmail(), SC_BAD_REQUEST, ReferralInformationResponseModel
                //                        .builder()
                //                        .build()),
                // of(partnerId, EMPTY, generateRandomEmail(), SC_BAD_REQUEST, ReferralInformationResponseModel
                //                        .builder()
                //                        .build()),
                of(partnerId, LOCATION_US, null, SC_BAD_REQUEST, ReferralInformationResponseModel
                        .builder()
                        .build()),
                of(partnerId, LOCATION_US, EMPTY, SC_BAD_REQUEST, ReferralInformationResponseModel
                        .builder()
                        .build()),
                of(null, null, null, SC_BAD_REQUEST, ReferralInformationResponseModel
                        .builder()
                        .build()),
                of(EMPTY, EMPTY, EMPTY, SC_BAD_REQUEST, ReferralInformationResponseModel
                        .builder()
                        .build())
        );
    }

    static Stream<Arguments> getPostBonusToCustomerInvalidInputData() {
        return Stream.of(
                of(null, LOCATION_US, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, MVN_CURRENCY, SC_OK,
                        new BonusCustomerResponseModel[]{
                                BonusCustomerResponseModel
                                        .builder()
                                        .bonusCustomerSeqNumber(1)
                                        .customerEmail(email)
                                        .build()}),
                of(EMPTY, LOCATION_US, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, MVN_CURRENCY,
                        SC_OK,
                        new BonusCustomerResponseModel[]{
                                BonusCustomerResponseModel
                                        .builder()
                                        .customerId(EMPTY)
                                        .bonusCustomerSeqNumber(1)
                                        .customerEmail(email)
                                        .build()})
        );
    }

    static Stream<Arguments> getPostBonusToCustomerInvalidInputData2() {
        return Stream.of(
                of(partnerId, EMPTY, POSITION_ID, generateRandomEmail(), FIAT_AMOUNT_ZERO, FAKE_CURRENCY, SC_OK,
                        BonusCustomerResponseModel
                                .builder()
                                .customerEmail(email)
                                .customerId(customerId)
                                .bonusCustomerSeqNumber(1)
                                .build()),
                // TODO: now these two return 200, check when functionality is extended
                /*
                of(partnerId, LOCATION_US, POSITION_ID, null, FIAT_AMOUNT_ZERO, MVN_CURRENCY, SC_BAD_REQUEST,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body("some email field", containsString(
                                        "some email message")))),
                of(partnerId, LOCATION_US, POSITION_ID, EMPTY, FIAT_AMOUNT_ZERO, MVN_CURRENCY, SC_BAD_REQUEST,
                        (Consumer<ValidatableResponse>) (response -> response
                                .body("some email field", containsString(
                                        "some email message")))),
                 */
                of(null, null, null, null, FIAT_AMOUNT_ZERO, null, SC_OK,
                        BonusCustomerResponseModel
                                .builder()
                                .bonusCustomerSeqNumber(1)
                                .build()),
                of(EMPTY, EMPTY, EMPTY, EMPTY, FIAT_AMOUNT_ZERO, EMPTY, SC_OK,
                        BonusCustomerResponseModel
                                .builder()
                                .customerId(EMPTY)
                                .customerEmail(EMPTY)
                                .bonusCustomerSeqNumber(1)
                                .build())
        );
    }

    @BeforeEach
    void setUp() {
        partnerId = getRandomUuid();
        partnerPassword = generateValidPassword();
        email = generateRandomEmail();
        phone = FakerUtils.phoneNumber;
        customerInfo = registerDefaultVerifiedCustomer();
        customerId = customerInfo.getCustomerId();

        val partnerCredentials = createPartnerCredentials(partnerId, partnerPassword, CLIENT_PREFIX.apply(partnerId));
        assertEquals(NONE, partnerCredentials.getError());
        val locationSuffix = generateRandomString(10);
        externalLocationId = SOME_EXTERNAL_ID + locationSuffix;
        partnerId = getRandomUuid();
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                locationSuffix);
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @Disabled("TODO: needs investigation")
    @ParameterizedTest(name = "Run {index}: {0} partnerId={1}, String email={2}, phone={3}")
    @MethodSource("getCustomerInfoTestData")
    @Tag(SMOKE_TEST)
    @UserStoryId(2088)
    void shouldGetCustomerInfo(String description, String partnerIdParam, String emailParam,
            String phoneParam, CustomerInformationRequestModel requestModel,
            CustomerInformationResponseModel expectedResult, Consumer<Void> action) {

        if (null != action) {
            action.accept(null);
        }

        val actualResult = getCustomerInfo(requestModel, partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {2088, 2689})
    void shouldGetCustomerBalance_Deprecated() {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(TOKENS_ZERO)
                .fiatBalance(FIAT_BALANCE_ZERO)
                .fiatCurrency(AED_CURRENCY)
                .status(CustomerBalanceStatus.OK)
                .build();

        val actualResult = getCustomerBalance_Deprecated(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerId)
                        .externalLocationId(LOCATION_US)
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
    @UserStoryId(3816)
    void shouldGetCustomerBalance() {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(TOKENS_ZERO)
                .fiatBalance(FIAT_BALANCE_ZERO)
                .fiatCurrency(AED_CURRENCY)
                .status(CustomerBalanceStatus.OK)
                .build();

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerId)
                        .externalLocationId(LOCATION_US)
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
    void shouldNotGetCustomerBalanceDueToInvalidCurrency_Deprecated() {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(TOKENS_ZERO)
                .fiatBalance(FIAT_BALANCE_ZERO)
                .fiatCurrency(USD_CURRENCY)
                .status(CustomerBalanceStatus.OK)
                .build();

        val actualResult = getCustomerBalance_Deprecated(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerId)
                        .externalLocationId(LOCATION_US)
                        .currency(USD_CURRENCY)
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
    @UserStoryId(3816)
    void shouldNotGetCustomerBalanceDueToInvalidCurrency() {
        val expectedResult = CustomerBalanceResponseModel
                .builder()
                .tokens(TOKENS_ZERO)
                .fiatBalance(FIAT_BALANCE_ZERO)
                .fiatCurrency(USD_CURRENCY)
                .status(CustomerBalanceStatus.OK)
                .build();

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(partnerId)
                        .externalLocationId(LOCATION_US)
                        .currency(USD_CURRENCY)
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

    @Disabled("TODO: wrong enum representaion")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2088)
    void shouldGetReferralInformation() {
        // TODO: wnen it's possible compare with real data
        val expectedResult = ReferralInformationResponseModel
                .builder()
                .referrals(new ReferralInfo[]{ReferralInfo.builder().referralId(customerId).build()})
                .status(ReferralInformationStatus.OK)
                .build();

        val actualResult = getReferralInformation(ReferralInformationRequestModel
                .builder()
                .customerId(email)
                .locationId(externalLocationId)
                .partnerId(partnerData.getId())
                .build(), partnerToken)
                .then()

                // TODO: enum
                /*
                {
                    "Referrals": [

                    ],
                    "Status": 3
                }
                */
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2088)
    void shouldBeAbleToPostBonusToCustomer() {
        val expectedResult = BonusCustomerResponseModel
                .builder()
                .customerId(customerId)
                .customerEmail(email)
                .bonusCustomerSeqNumber(1)
                .build();

        val actualResult = postTriggerBonusToCustomer(BonusCustomersRequestModel
                .builder()
                .bonusCustomers(new BonusCustomerModel[]{
                        new BonusCustomerModel(customerId, email, DEFAULT_FIAT_AMOUNT, AED_CURRENCY,
                                Instant.now().toString(),
                                partnerData.getId(), locationId, POSITION_ID)})
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusCustomerResponseModel[].class)[0];

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {2088, 2654, 2689})
    void shouldNotBeAbleToPostBonusToCustomerWithInvalidCurrency() {
        val expectedResult = BonusCustomerResponseModel
                .builder()
                .customerId(customerId)
                .customerEmail(email)
                .bonusCustomerSeqNumber(1)
                .build();

        val actualResult = postTriggerBonusToCustomer(BonusCustomersRequestModel
                .builder()
                .bonusCustomers(new BonusCustomerModel[]{
                        new BonusCustomerModel(customerId, email, DEFAULT_FIAT_AMOUNT, FAKE_CURRENCY,
                                Instant.now().toString(),
                                partnerData.getId(), locationId, POSITION_ID)})
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BonusCustomerResponseModel[].class)[0];

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2088)
    void shouldNotGetCustomerInfoOnInvalidInput() {
        getCustomerInfo(CustomerInformationRequestModel
                .builder()
                .customerId(EMPTY)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("\"\"[0]", containsString(MUST_HAVE_VALUE_ERROR_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("getInvalidCustomerInfoInputData")
    @UserStoryId(2088)
    void shouldNotGetCustomerInfoForNonExistingCustomer(String customerIdParam) {
        val expectedResult = CustomerInformationResponseModel
                .builder()
                .customerId(null)
                .status(CustomerBalanceStatus.CUSTOMER_NOT_FOUND)
                .build();

        val actualResult = getCustomerInfo(CustomerInformationRequestModel
                .builder()
                .customerId(customerIdParam)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getCustomerBalanceInvalidInputData")
    @UserStoryId(2088)
    void shouldNotGetCustomerBalanceOnInvalidInput_Deprecated(String customerIdParam, String locationIdParam,
            String currencyParam, int status, CustomerBalanceResponseModel expectedResult) {

        val actualResult = getCustomerBalance_Deprecated(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(customerIdParam)
                        .externalLocationId(locationIdParam)
                        .currency(currencyParam)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getCustomerBalanceInvalidInputData")
    @UserStoryId(3816)
    void shouldNotGetCustomerBalanceOnInvalidInput(String customerIdParam, String locationIdParam,
            String currencyParam, int status, CustomerBalanceResponseModel expectedResult) {

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(customerIdParam)
                        .externalLocationId(locationIdParam)
                        .currency(currencyParam)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getCustomerBalanceInvalidInputData2")
    @UserStoryId(storyId = {2088, 2689})
    void shouldNotGetCustomerBalanceOnInvalidInput_Deprecated1(String customerIdParam, String locationIdParam,
            String currencyParam, int status, CustomerBalanceResponseModel expectedResult) {

        val actualResult = getCustomerBalance_Deprecated(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(customerIdParam)
                        .externalLocationId(locationIdParam)
                        .currency(currencyParam)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getCustomerBalanceInvalidInputData2")
    @UserStoryId(3816)
    void shouldNotGetCustomerBalanceOnInvalidInput1(String customerIdParam, String locationIdParam,
            String currencyParam, int status, CustomerBalanceResponseModel expectedResult) {

        val actualResult = getCustomerBalance(CustomerBalanceRequestModel
                        .builder()
                        .partnerId(customerIdParam)
                        .externalLocationId(locationIdParam)
                        .currency(currencyParam)
                        .build(),
                customerId,
                partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(CustomerBalanceResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getReferralInformationInvalidInputData")
    @UserStoryId(2088)
    void shouldNotGetReferralInformationOnInvalidInput(String customerIdParam, String locationIdParam,
            String emailParam, int status, ReferralInformationResponseModel expectedResult) {

        val actualResult = getReferralInformation(ReferralInformationRequestModel
                .builder()
                .customerId(emailParam)
                .locationId(locationIdParam)
                .partnerId(customerIdParam)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(ReferralInformationResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getPostBonusToCustomerInvalidInputData")
    @UserStoryId(storyId = {2088, 2689})
    void shouldNotBeAbleToPostBonusToCustomerOnInvalidInput(String customerIdParam, String locationIdParam,
            String positionParam, String emailParam, Double fiatAmountParam, String currencyParam, int status,
            BonusCustomerResponseModel[] expectedResult) {

        expectedResult[0].setCustomerEmail(emailParam);

        val actualResponse = postTriggerBonusToCustomer(BonusCustomersRequestModel
                .builder()
                .bonusCustomers(new BonusCustomerModel[]{
                        new BonusCustomerModel(customerIdParam, emailParam, fiatAmountParam, currencyParam,
                                Instant.now().toString(),
                                customerIdParam, locationIdParam, positionParam)})
                .build(), partnerToken)
                .thenReturn();
        val actualResult = actualResponse
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(BonusCustomerResponseModel[].class);

        assertEquals(expectedResult[0], actualResult[0]);
    }

    @ParameterizedTest
    @MethodSource("getPostBonusToCustomerInvalidInputData2")
    @UserStoryId(storyId = {2088, 2689})
    void shouldNotBeAbleToPostBonusToCustomerOnInvalidInput(String customerIdParam, String locationIdParam,
            String positionParam, String emailParam, Double fiatAmountParam, String currencyParam, int status,
            BonusCustomerResponseModel expectedResult) {

        expectedResult.setCustomerEmail(emailParam);

        val actualResponse = postTriggerBonusToCustomer(BonusCustomersRequestModel
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
                .statusCode(status);

        assertEquals(expectedResult, actualResponse.as(BonusCustomerResponseModel[].class)[0]);
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
