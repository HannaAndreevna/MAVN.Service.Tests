package com.lykke.tests.api.service.admin.partners;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.PASSWORD_REG_EX;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.admin.PartnersUtils.addNewPartner;
import static com.lykke.tests.api.service.admin.PartnersUtils.generateClientId;
import static com.lykke.tests.api.service.admin.PartnersUtils.generateClientSecret;
import static com.lykke.tests.api.service.admin.PartnersUtils.getPartnerById;
import static com.lykke.tests.api.service.admin.PartnersUtils.getPartnerById_Response;
import static com.lykke.tests.api.service.admin.PartnersUtils.getPartnerIdByName;
import static com.lykke.tests.api.service.admin.PartnersUtils.getPartnersPaginated;
import static com.lykke.tests.api.service.admin.PartnersUtils.getPartnersPaginated_ValidationResponse;
import static com.lykke.tests.api.service.admin.PartnersUtils.updatePartner;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.github.javafaker.Faker;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.enums.BusinessVertical;
import com.lykke.tests.api.service.admin.LoginAdminUtils;
import com.lykke.tests.api.service.admin.model.partners.LocationCreateRequest;
import com.lykke.tests.api.service.admin.model.partners.LocationEditRequest;
import com.lykke.tests.api.service.admin.model.partners.PartnerCreateRequest;
import com.lykke.tests.api.service.admin.model.partners.PartnerUpdateRequest;
import com.lykke.tests.api.service.admin.model.partners.PartnersListRequest;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PartnersTests extends BaseApiTest {

    private static final String ID_0_FIELD = "id[0]";
    private static final String INVALID_ID_MSG = "The value '%S' is not valid.";
    private static String token;

    static Stream<Arguments> getInvalidPaginationParameters() {
        return Stream.of(
                of(INVALID_CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY),
                of(INVALID_CURRENT_PAGE_UPPER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_LOWER_BOUNDARY),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_UPPER_BOUNDARY)
        );
    }

    @BeforeAll
    static void setup() {
        token = LoginAdminUtils.getAdminToken();
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(storyId = 1279)
    void shouldNotGetPartners_invalidRequest(int currentPage, int pageSize) {
        val partnersListObj = PartnersListRequest
                .builder()
                .pageSize(pageSize)
                .currentPage(currentPage)
                .build();

        val actualResult = getPartnersPaginated_ValidationResponse(partnersListObj, token);

        assertEquals(partnersListObj.getValidationResponse(), actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1279)
    void shouldGetPartners() {
        val partnersListObj = PartnersListRequest
                .builder()
                .pageSize(10)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .build();

        val actualResult = getPartnersPaginated(partnersListObj, token);

        //TODO: add more assertions
        assertAll(
                () -> assertNotNull(actualResult.getPartners())
        );
    }

    @Test
    @UserStoryId(storyId = {1279, 4291})
    void shouldAddNewPartners() {

        val locationCreateRequest = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(15))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerCreateObj = PartnerCreateRequest
                .partnerCreateRequestBuilder()
                .locations(new LocationCreateRequest[]{locationCreateRequest})
                .name(FakerUtils.companyName)
                .amountInCurrency(10.0)
                .amountInTokens("25")
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(generateValidPassword())
                .businessVertical(BusinessVertical.HOSPITALITY)
                .build();

        addNewPartner(partnerCreateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(storyId = {3091, 4291})
    void shouldCreatePartnerWithLongAmounts() {
        val locationCreateRequest = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(15))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerCreateObj = PartnerCreateRequest
                .partnerCreateRequestBuilder()
                .locations(new LocationCreateRequest[]{locationCreateRequest})
                .name(FakerUtils.companyName)
                .amountInCurrency(12.3)
                .amountInTokens("2.86")
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(generateValidPassword())
                .businessVertical(BusinessVertical.HOSPITALITY)
                .build();

        addNewPartner(partnerCreateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(storyId = {1279, 4291})
    void shouldAddAndGetPartnersWithMultipleLocations() {
        val name = FakerUtils.companyName;

        val locationCreateObj1 = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val locationCreateObj2 = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val locationCreateObj3 = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerCreateObj = PartnerCreateRequest
                .partnerCreateRequestBuilder()
                .locations(new LocationCreateRequest[]{locationCreateObj1, locationCreateObj2, locationCreateObj3})
                .name(name)
                .amountInCurrency(10.0)
                .amountInTokens("25")
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(generateValidPassword())
                .businessVertical(BusinessVertical.HOSPITALITY)
                .build();

        addNewPartner(partnerCreateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val id = getPartnerIdByName(name, token);
        val actualResult = getPartnerById(id, token);

        assertAll(
                () -> assertEquals(id, actualResult.getId()),
                () -> assertEquals(partnerCreateObj.getName(), actualResult.getName()),
                () -> assertEquals(partnerCreateObj.getBusinessVertical(), actualResult.getBusinessVertical()),
                () -> assertEquals(partnerCreateObj.getClientId(), actualResult.getClientId()),
                () -> assertNull(actualResult.getClientSecret()),
                () -> assertEquals(partnerCreateObj.getDescription(), actualResult.getDescription()),
                () -> assertEquals(partnerCreateObj.getLocations().length, actualResult.getLocations().length)
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1279, 4291})
    void shouldGetPartnerById() {
        val locationName = FakerUtils.title;
        val address = FakerUtils.address;
        val firstName = FakerUtils.firstName;
        val lastName = FakerUtils.lastName;
        val phone = FakerUtils.phoneNumber;
        val email = generateRandomEmail();
        val partnerName = FakerUtils.companyName;
        val tokensRate = "125";
        val currencyRate = "233";
        val description = FakerUtils.randomQuote;
        val businessVertical = BusinessVertical.HOSPITALITY;

        val locationCreateRequest = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(locationName)
                .address(address)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .externalId(generateRandomEmail())
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerCreateObj = PartnerCreateRequest
                .partnerCreateRequestBuilder()
                .locations(new LocationCreateRequest[]{locationCreateRequest})
                .name(partnerName)
                .amountInCurrency(Double.valueOf(currencyRate))
                .amountInTokens(tokensRate)
                .useGlobalCurrencyRate(false)
                .description(description)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(generateValidPassword())
                .businessVertical(businessVertical)
                .build();

        addNewPartner(partnerCreateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val id = getPartnerIdByName(partnerName, token);

        val actualResult = getPartnerById(id, token);
        val expectedLocation = partnerCreateObj.getLocations()[0];
        val actualLocation = actualResult.getLocations()[0];

        assertAll(
                () -> assertEquals(id, actualResult.getId()),
                () -> assertEquals(partnerCreateObj.getName(), actualResult.getName()),
                () -> assertEquals(partnerCreateObj.getBusinessVertical(), actualResult.getBusinessVertical()),
                () -> assertEquals(partnerCreateObj.getClientId(), actualResult.getClientId()),
                () -> assertNull(actualResult.getClientSecret()),
                () -> assertEquals(partnerCreateObj.getDescription(), actualResult.getDescription()),
                () -> assertEquals(expectedLocation.getAddress(), actualLocation.getAddress()),
                () -> assertEquals(expectedLocation.getEmail(), actualLocation.getEmail()),
                () -> assertEquals(expectedLocation.getFirstName(), actualLocation.getFirstName()),
                () -> assertEquals(expectedLocation.getLastName(), actualLocation.getLastName()),
                () -> assertEquals(expectedLocation.getPhone(), actualLocation.getPhone())
        );
    }

    @Test
    @UserStoryId(storyId = 1279)
    void shouldGenerateClientSecret() {
        generateClientSecret(token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(matchesPattern(PASSWORD_REG_EX));
    }

    @Test
    @UserStoryId(storyId = 1279)
    void shouldGenerateClientId() {
        generateClientId(token)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(Matchers.isA(String.class));
    }

    @Test
    @UserStoryId(storyId = {1279, 4291})
    void shouldUpdatePartner() {
        val faker = new Faker();
        val newLocationName = faker.country().capital();
        val newAddress = faker.address().fullAddress();
        val newFirstName = faker.name().firstName();
        val newLastName = faker.name().lastName();
        val newPhone = faker.phoneNumber().phoneNumber().replace(".", "-");

        val newEmail = generateRandomEmail();
        val partnerName = faker.company().name();
        final Integer newTokensRate = 521;
        final Double newCurrencyRate = 1024.0;
        val newDescription = faker.yoda().quote();
        val newBusinessVertical = BusinessVertical.HOSPITALITY;
        val newClientId = UUID.randomUUID().toString();
        val newClientSecret = generateValidPassword();

        val locationCreateRequest = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerCreateObj = PartnerCreateRequest
                .partnerCreateRequestBuilder()
                .locations(new LocationCreateRequest[]{locationCreateRequest})
                .name(partnerName)
                .amountInCurrency(10.0)
                .amountInTokens("25")
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(generateValidPassword())
                .businessVertical(BusinessVertical.HOSPITALITY)
                .build();

        addNewPartner(partnerCreateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val id = getPartnerIdByName(partnerName, token);
        val locationId = getPartnerById(id, token).getLocations()[0].getId();

        val locationEditRequest = LocationEditRequest
                .locationEditRequestBuilder()
                .id(locationId)
                .name(newLocationName)
                .address(newAddress)
                .firstName(newFirstName)
                .lastName(newLastName)
                .phone(newPhone)
                .email(newEmail)
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val newLocationCreateObj = LocationEditRequest
                .locationEditRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerUpdateObj = PartnerUpdateRequest
                .partnerUpdateRequestBuilder()
                .id(id)
                .locations(new LocationEditRequest[]{locationEditRequest, newLocationCreateObj})
                .name(partnerName)
                .description(newDescription)
                .clientId(newClientId.toString())
                .clientSecret(newClientSecret.toString())
                .businessVertical(newBusinessVertical)
                .amountInTokens(newTokensRate.toString())
                .amountInCurrency(newCurrencyRate)
                .build();

        updatePartner(partnerUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getPartnerById(id, token);
        val expectedResultLocation = partnerUpdateObj.getLocations()[0];
        val actualResultLocation = actualResult.getLocations()[0];

        assertAll(
                () -> assertEquals(id, actualResult.getId()),
                () -> assertEquals(partnerUpdateObj.getName(), actualResult.getName()),
                () -> assertEquals(partnerUpdateObj.getBusinessVertical(), actualResult.getBusinessVertical()),
                () -> assertEquals(partnerUpdateObj.getClientId(), actualResult.getClientId()),
                () -> assertNull(actualResult.getClientSecret()),
                () -> assertEquals(partnerUpdateObj.getDescription(), actualResult.getDescription()),
                () -> assertEquals(partnerUpdateObj.getAmountInCurrency(), actualResult.getAmountInCurrency()),
                () -> assertEquals(partnerUpdateObj.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(partnerUpdateObj.getLocations().length, actualResult.getLocations().length)
        );
    }

    @Test
    @UserStoryId(storyId = {1279, 4291})
    void shouldUpdatePartnerAndAddNewLocations() {
        val faker = new Faker();
        val newLocationName = faker.country().capital();
        val newAddress = faker.address().fullAddress();
        val newFirstName = faker.name().firstName();
        val newLastName = faker.name().lastName();
        val newPhone = faker.phoneNumber().phoneNumber().replace(".", "-");

        val newEmail = generateRandomEmail();
        val partnerName = faker.company().name();
        final Integer newTokensRate = 521;
        final Double newCurrencyRate = 1024.0;
        val newDescription = faker.yoda().quote();
        val newBusinessVertical = BusinessVertical.HOSPITALITY;
        val newClientId = UUID.randomUUID().toString();
        val newClientSecret = generateValidPassword();

        val locationCreateRequest = LocationCreateRequest
                .locationCreateRequestBuilder()
                .name(FakerUtils.title)
                .address(FakerUtils.address)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phone(FakerUtils.phoneNumber)
                .email(generateRandomEmail())
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerCreateObj = PartnerCreateRequest
                .partnerCreateRequestBuilder()
                .locations(new LocationCreateRequest[]{locationCreateRequest})
                .name(partnerName)
                .amountInCurrency(10.0)
                .amountInTokens("25")
                .useGlobalCurrencyRate(false)
                .description(FakerUtils.randomQuote)
                .clientId(UUID.randomUUID().toString())
                .clientSecret(generateValidPassword())
                .businessVertical(BusinessVertical.HOSPITALITY)
                .build();

        addNewPartner(partnerCreateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val id = getPartnerIdByName(partnerName, token);
        val locationId = getPartnerById(id, token).getLocations()[0].getId();

        val locationEditRequest = LocationEditRequest
                .locationEditRequestBuilder()
                .id(locationId)
                .name(newLocationName)
                .address(newAddress)
                .firstName(newFirstName)
                .lastName(newLastName)
                .phone(newPhone)
                .email(newEmail)
                .externalId(generateRandomString(10))
                .accountingIntegrationCode(generateRandomString(10))
                .build();

        val partnerUpdateObj = PartnerUpdateRequest
                .partnerUpdateRequestBuilder()
                .id(id)
                .locations(new LocationEditRequest[]{locationEditRequest})
                .name(partnerName)
                .description(newDescription)
                .clientId(newClientId.toString())
                .clientSecret(newClientSecret.toString())
                .businessVertical(newBusinessVertical)
                .amountInTokens(newTokensRate.toString())
                .amountInCurrency(newCurrencyRate)
                .build();

        updatePartner(partnerUpdateObj, token)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        val actualResult = getPartnerById(id, token);
        val expectedResultLocation = partnerUpdateObj.getLocations()[0];
        val actualResultLocation = actualResult.getLocations()[0];

        assertAll(
                () -> assertEquals(id, actualResult.getId()),
                () -> assertEquals(partnerUpdateObj.getName(), actualResult.getName()),
                () -> assertEquals(partnerUpdateObj.getBusinessVertical(), actualResult.getBusinessVertical()),
                () -> assertEquals(partnerUpdateObj.getClientId(), actualResult.getClientId()),
                () -> assertNull(actualResult.getClientSecret()),
                () -> assertEquals(partnerUpdateObj.getDescription(), actualResult.getDescription()),
                () -> assertEquals(partnerUpdateObj.getAmountInCurrency(), actualResult.getAmountInCurrency()),
                () -> assertEquals(partnerUpdateObj.getAmountInTokens(), actualResult.getAmountInTokens()),
                () -> assertEquals(expectedResultLocation.getAddress(), actualResultLocation.getAddress()),
                () -> assertEquals(expectedResultLocation.getEmail(), actualResultLocation.getEmail()),
                () -> assertEquals(expectedResultLocation.getFirstName(), actualResultLocation.getFirstName()),
                () -> assertEquals(expectedResultLocation.getLastName(), actualResultLocation.getLastName()),
                () -> assertEquals(expectedResultLocation.getPhone(), actualResultLocation.getPhone()),
                () -> assertEquals(locationId, actualResultLocation.getId())
        );
    }

    @Test
    @UserStoryId(storyId = 1279)
    void shouldNotGetPartnerWhenIdIsNotValid() {
        val invalidId = "9819a210-5914-4183-8d16-f0b9d1c4684";
        getPartnerById_Response(invalidId, token)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ID_0_FIELD, Matchers.equalToIgnoringCase(String.format(INVALID_ID_MSG, invalidId)));
    }

    //TODO: Create tests for field validations
}
