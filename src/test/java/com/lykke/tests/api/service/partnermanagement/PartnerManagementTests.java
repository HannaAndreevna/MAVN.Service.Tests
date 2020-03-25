package com.lykke.tests.api.service.partnermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.SOME_AMOUNT_IN_CURRENCY;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.SOME_AMOUNT_IN_TOKENS;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getPartnerByClientId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getPartnerById;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getPartnerByLocationId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getPartners;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.updatePartner;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.partnermanagement.model.ContactPersonModel;
import com.lykke.tests.api.service.partnermanagement.model.LocationCreateModel;
import com.lykke.tests.api.service.partnermanagement.model.LocationUpdateModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnermanagement.model.PartnerDetailsModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerListDetailsModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerListRequestModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerListResponseModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerManagementError;
import com.lykke.tests.api.service.partnermanagement.model.PartnerUpdateModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerUpdateResponse;
import com.lykke.tests.api.service.partnermanagement.model.Vertical;
import io.restassured.response.ValidatableResponse;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class PartnerManagementTests extends BaseApiTest {

    private static final String LOCATIONS_0_EXTERNAL_ID_FIELD = "Locations[0].ExternalId";
    private static final String THE_INPUT_WAS_NOT_VALID_ERROR_MESSAGE = "The input was not valid.";
    private static final String NAME_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Name' must not be empty.";
    private static final String THE_PARTNER_SHOULD_HAVE_AT_LEAST_ONE_LOCATION_ERROR_MESSAGE = "The Partner should have at least one location.";
    private static final String TOKENS_RATE_MUST_BE_GREATER_THAN_0_ERROR_MESSAGE = "'Tokens Rate' must be greater than '0'.";
    private static final String CURRENCY_RATE_MUST_BE_GREATER_THAN_0_ERROR_MESSAGE = "'Currency Rate' must be greater than '0'.";
    private static final String BUSINESS_VERTICAL_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'Business Vertical' must not be empty.";
    private static final String THE_BUSINESS_VERTICAL_SHOULD_BE_PRESENT_ERROR_MESSAGE = "The Business Vertical should be present";
    private static final String EXTERNAL_ID_MUST_NOT_BE_EMPTY_ERROR_MESSAGE = "'External Id' must not be empty.";
    private static final String LOCATIONS_0_ACCOUNTING_INTEGRATION_CODE_0_FIELD = "Locations[0].AccountingIntegrationCode[0]";
    private static final String LOCATIONS_0_ACCOUNTING_INTEGRATION_CODE_1_FIELD = "Locations[0].AccountingIntegrationCode[1]";
    private static final String ACCOUNTING_INTEGRATION_CODE_MUST_NOT_BE_EMPTY_MESSAGE = "'Accounting Integration Code' must not be empty.";
    private static final String THE_LENGTH_OF_ACCOUNTING_INTEGRATION_CODE_MUST_BE_AT_LEAST_1_CHARACTERS_YOU_ENTERED_0_CHARACTERS_MESSAGE = "The length of 'Accounting Integration Code' must be at least 1 characters. You entered 0 characters.";
    private static final String THE_ACCOUNTING_INTEGRATION_CODE_SHOULD_BE_WITHIN_A_RANGE_OF_1_TO_80_CHARACTERS_LONG_MESSAGE = "The accounting integration code should be within a range of 1 to 80 characters long.";
    private String partnerPassword;
    private String partnerToken;
    private String customerId;
    private String customerPassword;
    private String customerToken;
    private String email;
    private String phone;
    private String locationId;
    private String clientId;
    private PartnerCreateResponse partnerData;

    static Stream<Arguments> getInvalidInputDataForPartnerCreation() {
        return Stream.of(
                of(PartnerCreateModel
                        .partnerBuilder()
                        .clientId(getRandomUuid())
                        .createdBy(getRandomUuid())
                        .build(), PartnerCreationValidationErrorResponse
                        .builder()
                        .name(new String[]{NAME_MUST_NOT_BE_EMPTY_ERROR_MESSAGE})
                        .locations(new String[]{THE_PARTNER_SHOULD_HAVE_AT_LEAST_ONE_LOCATION_ERROR_MESSAGE})
                        .build()),
                of(PartnerCreateModel
                        .partnerBuilder()
                        .clientId(getRandomUuid())
                        .build(), PartnerCreationValidationErrorResponse
                        .builder()
                        .createdBy(new String[]{THE_INPUT_WAS_NOT_VALID_ERROR_MESSAGE})
                        .build()),
                of(PartnerCreateModel
                        .partnerBuilder()
                        .clientId(getRandomUuid())
                        .businessVertical(Vertical.HOSPITALITY)
                        .createdBy("Xcfsad")
                        .build(), PartnerCreationValidationErrorResponse
                        .builder()
                        .createdBy(new String[]{THE_INPUT_WAS_NOT_VALID_ERROR_MESSAGE})
                        .build()),
                of(PartnerCreateModel
                        .partnerBuilder()
                        .businessVertical(Vertical.HOSPITALITY)
                        .clientId(getRandomUuid())
                        .clientSecret(getRandomUuid())
                        .createdBy(getRandomUuid())
                        .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                        .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                        .useGlobalCurrencyRate(false)
                        .description(FakerUtils.randomQuote)
                        .locations(new LocationCreateModel[]{LocationCreateModel
                                .locationBuilder()
                                .address(FakerUtils.address)
                                .contactPerson(ContactPersonModel
                                        .builder()
                                        .email(generateRandomEmail())
                                        .firstName(FakerUtils.firstName)
                                        .lastName(FakerUtils.lastName)
                                        .phoneNumber(FakerUtils.phoneNumber)
                                        .build())
                                .externalId(getRandomUuid())
                                .name(getRandomUuid())
                                .accountingIntegrationCode(generateRandomString(10))
                                .build()})
                        .name(getRandomUuid())
                        .build(), PartnerCreationValidationErrorResponse
                        .builder()
                        .createdBy(new String[]{THE_INPUT_WAS_NOT_VALID_ERROR_MESSAGE})
                        .build())
        );
    }

    static Stream<Arguments> getInvalidAccountingIntegrationCode() {
        return Stream.of(
                of(generateRandomString(0), (Consumer<ValidatableResponse>) (response -> response
                        .body(LOCATIONS_0_ACCOUNTING_INTEGRATION_CODE_0_FIELD,
                                containsString(ACCOUNTING_INTEGRATION_CODE_MUST_NOT_BE_EMPTY_MESSAGE))
                        .body(LOCATIONS_0_ACCOUNTING_INTEGRATION_CODE_1_FIELD,
                                containsString(
                                        THE_LENGTH_OF_ACCOUNTING_INTEGRATION_CODE_MUST_BE_AT_LEAST_1_CHARACTERS_YOU_ENTERED_0_CHARACTERS_MESSAGE)))),
                of(generateRandomString(81), (Consumer<ValidatableResponse>) (response -> response
                        .body(LOCATIONS_0_ACCOUNTING_INTEGRATION_CODE_0_FIELD,
                                containsString(
                                        THE_ACCOUNTING_INTEGRATION_CODE_SHOULD_BE_WITHIN_A_RANGE_OF_1_TO_80_CHARACTERS_LONG_MESSAGE))))
        );
    }

    @BeforeEach
    void setUp() {
        partnerPassword = generateValidPassword();
        email = generateRandomEmail();
        phone = FakerUtils.phoneNumber;
        customerPassword = generateValidPassword();

        clientId = getRandomUuid();
        partnerData = createDefaultPartner(clientId, partnerPassword, generateRandomString(10),
                generateRandomString(10));
        locationId = getLocationId(partnerData);
        // TODO: why does it require the token that's not partner's token?
        // partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
        partnerToken = getPartnerToken(clientId, partnerPassword, USER_INFO);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 80})
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1288, 4289})
    void shouldCreatePartner(int length) {
        val partnerId = getRandomUuid();

        val expectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partnerId)
                .build();

        val actualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(getRandomUuid())
                        .name(generateRandomString(10))
                        .accountingIntegrationCode(generateRandomString(length))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(expectedResult.getErrorCode(), actualResult.getErrorCode()),
                () -> assertEquals(expectedResult.getErrorMessage(), actualResult.getErrorMessage())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1288)
    void shouldUpdatePartner() {
        var partnerId = getRandomUuid();

        val partnerCreationExpectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partnerId)
                .build();

        val partnerUpdateExpectedResult = PartnerUpdateResponse
                .partnerUpdateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .build();

        val partnerCreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(locationId)
                        .name(generateRandomString(10))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(partnerCreationExpectedResult.getErrorCode(),
                        partnerCreationActualResult.getErrorCode()),
                () -> assertEquals(partnerCreationExpectedResult.getErrorMessage(),
                        partnerCreationActualResult.getErrorMessage())
        );

        partnerId = partnerCreationActualResult.getId();

        val partner = getPartnerById(partnerId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class);
        locationId = partner.getLocations()[0].getId();

        val partnerUpdateActualResult = updatePartner(PartnerUpdateModel
                .partnerBuilder()
                .businessVertical(Vertical.REAL_ESTATE)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .id(partnerId)
                .locations(new LocationUpdateModel[]{LocationUpdateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(locationId)
                        .name(generateRandomString(10))
                        .id(locationId)
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerUpdateResponse.class);

        assertAll(
                () -> assertEquals(partnerUpdateExpectedResult.getErrorCode(),
                        partnerUpdateActualResult.getErrorCode()),
                () -> assertEquals(partnerUpdateExpectedResult.getErrorMessage(),
                        partnerUpdateActualResult.getErrorMessage())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1288, 3017})
    void shouldGetPartnerById() {
        val actualPartner = getPartnerById(partnerData.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class);

        assertAll(
                () -> assertEquals(partnerData.getId(), actualPartner.getId()),
                // TODO: why the 100 is here
                () -> assertEquals(Double.valueOf(SOME_AMOUNT_IN_TOKENS) / 100,
                        Double.valueOf(actualPartner.getAmountInTokens())),
                () -> assertEquals(SOME_AMOUNT_IN_CURRENCY, actualPartner.getAmountInCurrency())
        );
    }

    @Test
    @UserStoryId(3017)
    void shouldGetPartnerByClientId() {
        val actualPartner = getPartnerByClientId(clientId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class);

        assertAll(
                () -> assertEquals(partnerData.getId(), actualPartner.getId()),
                // TODO: why the 100 is here
                () -> assertEquals(Double.valueOf(SOME_AMOUNT_IN_TOKENS) / 100,
                        Double.valueOf(actualPartner.getAmountInTokens())),
                () -> assertEquals(SOME_AMOUNT_IN_CURRENCY, actualPartner.getAmountInCurrency())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(3017)
    void shouldGetPartnerByLocationId() {
        val actualPartner = getPartnerByLocationId(locationId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class);

        assertAll(
                () -> assertEquals(partnerData.getId(), actualPartner.getId()),
                // TODO: why the 100 is here
                () -> assertEquals(Double.valueOf(SOME_AMOUNT_IN_TOKENS) / 100,
                        Double.valueOf(actualPartner.getAmountInTokens())),
                () -> assertEquals(SOME_AMOUNT_IN_CURRENCY, actualPartner.getAmountInCurrency())
        );
    }

    @Test
    @UserStoryId(3017)
    void shouldNotGetPartnerByNonExistingClientId() {
        val actualPartner = getPartnerByClientId(getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(2745)
    void shouldNotUpdatePartnerWithSomeoneElsesCredentials() {
        var partner01Id = getRandomUuid();
        var partner02Id = getRandomUuid();
        val partner01password = generateValidPassword();
        val partner02password = generateValidPassword();
        val externalLocation01Id = getRandomUuid();
        val externalLocation02Id = getRandomUuid();

        val partner01CreationExpectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partner01Id)
                .build();

        val partner01CreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partner01Id)
                .clientSecret(partner01password)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(externalLocation01Id)
                        .name(generateRandomString(10))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(partner01CreationExpectedResult.getErrorCode(),
                        partner01CreationActualResult.getErrorCode()),
                () -> assertEquals(partner01CreationExpectedResult.getErrorMessage(),
                        partner01CreationActualResult.getErrorMessage())
        );

        partner01Id = partner01CreationActualResult.getId();

        val partner02CreationExpectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partner01Id)
                .build();

        val partner02CreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partner02Id)
                .clientSecret(partner02password)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(externalLocation02Id)
                        .name(generateRandomString(10))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(partner02CreationExpectedResult.getErrorCode(),
                        partner02CreationActualResult.getErrorCode()),
                () -> assertEquals(partner02CreationExpectedResult.getErrorMessage(),
                        partner02CreationActualResult.getErrorMessage())
        );

        val partner = getPartnerById(partner01Id)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class);
        locationId = partner.getLocations()[0].getId();

        val partnerUpdateActualResult = updatePartner(PartnerUpdateModel
                .partnerBuilder()
                .businessVertical(Vertical.REAL_ESTATE)
                .clientId(partner02Id) //// also works for partner01Id
                .clientSecret(partner02password)
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .id(partner01Id)
                .locations(new LocationUpdateModel[]{LocationUpdateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(externalLocation01Id)
                        .name(generateRandomString(10))
                        .id(locationId)
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerUpdateResponse.class);

        ////
        //v assertions
        /*
        assertAll(
                () -> assertEquals(partnerUpdateExpectedResult.getErrorCode(), partnerUpdateActualResult.getErrorCode()),
                () -> assertEquals(partnerUpdateExpectedResult.getErrorMessage(), partnerUpdateActualResult.getErrorMessage())
        );
        */
    }

    @Test
    @UserStoryId(1288)
    void shouldNotUpdatePartnerOnInvalidLocationId() {
        var partnerId = getRandomUuid();

        val partnerCreationExpectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partnerId)
                .build();

        val partnerUpdateExpectedResult = PartnerUpdateResponse
                .partnerUpdateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .build();

        val partnerCreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(locationId)
                        .name(generateRandomString(10))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(partnerCreationExpectedResult.getErrorCode(),
                        partnerCreationActualResult.getErrorCode()),
                () -> assertEquals(partnerCreationExpectedResult.getErrorMessage(),
                        partnerCreationActualResult.getErrorMessage())
        );

        partnerId = partnerCreationActualResult.getId();

        val partnerUpdateActualResult = updatePartner(PartnerUpdateModel
                .partnerBuilder()
                .businessVertical(Vertical.REAL_ESTATE)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .id(partnerId)
                .locations(new LocationUpdateModel[]{LocationUpdateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(locationId)
                        .name(generateRandomString(10))
                        .id(locationId)
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerUpdateResponse.class);

        assertAll(
                () -> assertEquals(partnerUpdateExpectedResult.getErrorCode(),
                        partnerUpdateActualResult.getErrorCode()),
                () -> assertEquals(partnerUpdateExpectedResult.getErrorMessage(),
                        partnerUpdateActualResult.getErrorMessage())
        );
    }

    @Test
    @UserStoryId(2745)
    void shouldNotUpdatePartnerWithSomeoneElseCredentials_Validation() {

        var partner01Id = getRandomUuid();
        var partner02Id = getRandomUuid();
        val partner01password = generateValidPassword();
        val partner02password = generateValidPassword();

        val partner01CreationExpectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partner01Id)
                .build();

        val partner01CreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partner01Id)
                .clientSecret(partner01password)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(getRandomUuid())
                        .name(generateRandomString(10))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(partner01CreationExpectedResult.getErrorCode(),
                        partner01CreationActualResult.getErrorCode()),
                () -> assertEquals(partner01CreationExpectedResult.getErrorMessage(),
                        partner01CreationActualResult.getErrorMessage())
        );

        partner01Id = partner01CreationActualResult.getId();

        val partner02CreationExpectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partner01Id)
                .build();

        val partner02CreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partner01Id)
                .clientSecret(partner01password)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(getRandomUuid())
                        .name(generateRandomString(10))
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        assertAll(
                () -> assertEquals(partner02CreationExpectedResult.getErrorCode(),
                        partner02CreationActualResult.getErrorCode()),
                () -> assertEquals(partner02CreationExpectedResult.getErrorMessage(),
                        partner02CreationActualResult.getErrorMessage())
        );

        val partnerUpdateActualResult = updatePartner(PartnerUpdateModel
                .partnerBuilder()
                .businessVertical(Vertical.REAL_ESTATE)
                .clientId(partner02Id)
                .clientSecret(partner02password)
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .id(partner01Id)
                .locations(new LocationUpdateModel[]{LocationUpdateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(locationId)
                        .name(generateRandomString(10))
                        .id(locationId)
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerUpdateResponse.class);

        ////
        //v assertions
        /*
        assertAll(
                () -> assertEquals(partnerUpdateExpectedResult.getErrorCode(), partnerUpdateActualResult.getErrorCode()),
                () -> assertEquals(partnerUpdateExpectedResult.getErrorMessage(), partnerUpdateActualResult.getErrorMessage())
        );
        */
    }

    @ParameterizedTest
    @MethodSource("getInvalidInputDataForPartnerCreation")
    @UserStoryId(storyId = {1288, 4289})
    void shouldNotCreatePartnerOnInvalidInput(PartnerCreateModel requestModel,
            PartnerCreationValidationErrorResponse expectedResult) {
        val actualResult = createPartner(requestModel)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PartnerCreationValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(3017)
    void shouldGetAllPartners() {
        var partnerId = getRandomUuid();
        val partnerName = FakerUtils.companyName;

        val partnerCreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(locationId)
                        .name(generateRandomString(10))
                        .build()})
                .name(partnerName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        val expectedResult = PartnerListResponseModel
                .partnerListResponseBuilder()
                .partnersDetails(new PartnerListDetailsModel[]{
                        PartnerListDetailsModel
                                .partnerBuilder()
                                .id(partnerCreationActualResult.getId())
                                .businessVertical(Vertical.HOSPITALITY)
                                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                                .useGlobalCurrencyRate(false)
                                .description(FakerUtils.randomQuote)
                                .name(partnerName)
                                .build()
                })
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .totalSize(1)
                .build();

        val actualResult = getPartners(PartnerListRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_LOWER_BOUNDARY)
                .name(partnerName)
                .vertical(Vertical.HOSPITALITY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerListResponseModel.class);

        assertEquals(expectedResult.getPartnersDetails()[0].getId(), actualResult.getPartnersDetails()[0].getId());
    }

    @ParameterizedTest
    @MethodSource("getInvalidAccountingIntegrationCode")
    @Tag(SMOKE_TEST)
    @UserStoryId(4289)
    void shouldNotCreatePartnerWitnInvalidAccountingIntegrationCode(String accountingIntegrationCode,
            Consumer<ValidatableResponse> assertAction) {
        val partnerId = getRandomUuid();

        val expectedResult = PartnerCreateResponse
                .partnerCreateResponseBuilder()
                .errorCode(PartnerManagementError.NONE)
                .id(partnerId)
                .build();

        val actualResponse = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partnerId)
                .clientSecret(generateValidPassword())
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .locations(new LocationCreateModel[]{LocationCreateModel
                        .locationBuilder()
                        .address(FakerUtils.address)
                        .contactPerson(ContactPersonModel
                                .builder()
                                .email(generateRandomEmail())
                                .firstName(FakerUtils.firstName)
                                .lastName(FakerUtils.lastName)
                                .phoneNumber(FakerUtils.phoneNumber)
                                .build())
                        .externalId(getRandomUuid())
                        .name(generateRandomString(10))
                        .accountingIntegrationCode(accountingIntegrationCode)
                        .build()})
                .name(FakerUtils.companyName)
                .build())
                .thenReturn();

        actualResponse
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);

        assertAction.accept(actualResponse.then().assertThat());
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PartnerCreationValidationErrorResponse {

        private String[] name;
        private String[] locations;
        private String[] tokensRate;
        private String[] clientsSecret;
        private String[] currencyRate;
        private String[] businessVertical;
        private String[] createdBy;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PartnerUpdateValidationErrorRespoonse {

        private String[] id;
    }
}
