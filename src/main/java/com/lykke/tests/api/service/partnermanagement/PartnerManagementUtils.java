package com.lykke.tests.api.service.partnermanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.PartnerManagement.GENERATE_CLIENT_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.GENERATE_CLIENT_SECRET_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.LOGIN_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.PARTNERS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.PARTNERS_LIST_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.PARTNER_BY_CLIENT_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.PARTNER_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerManagement.PARTNER_BY_LOCATION_ID_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.partnermanagement.model.ContactPersonModel;
import com.lykke.tests.api.service.partnermanagement.model.LocationCreateModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnermanagement.model.PartnerDetailsModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerListRequestModel;
import com.lykke.tests.api.service.partnermanagement.model.PartnerUpdateModel;
import com.lykke.tests.api.service.partnermanagement.model.Vertical;
import com.lykke.tests.api.service.partnermanagement.model.auth.AuthenticateRequestModel;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

@UtilityClass
public class PartnerManagementUtils {

    public static final String SOME_AMOUNT_IN_TOKENS = "100.000000000000000000";
    public static final float SOME_AMOUNT_IN_CURRENCY = 150;
    public static final boolean USE_GLOBAL_CURRENCY_RATE = false;
    public static final String SOME_EXTERNAL_ID = "some external id ";

    public Response loginPartner(AuthenticateRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(LOGIN_API_PATH)
                .thenReturn();
    }

    public String generateClientId() {
        return getHeader()
                .contentType(ContentType.TEXT)
                .post(GENERATE_CLIENT_ID_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .asString();
    }

    public String generateClientSecret() {
        return getHeader()
                .contentType(ContentType.TEXT)
                .post(GENERATE_CLIENT_SECRET_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .asString();
    }

    @Step("Create partner")
    public static Response createPartner(PartnerCreateModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(PARTNERS_API_PATH)
                .thenReturn();
    }

    public Response updatePartner(PartnerUpdateModel requestModel) {
        return getHeader()
                .body(requestModel)
                .put(PARTNERS_API_PATH)
                .thenReturn();
    }

    public Response deletePartnerById(String partnerId) {
        return getHeader()
                .delete(PARTNER_BY_ID_API_PATH.apply(partnerId))
                .thenReturn();
    }

    public Response getPartners(PartnerListRequestModel requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .get(PARTNERS_API_PATH)
                .thenReturn();
    }

    @Step("Create partner by partnerId {partnerId}")
    public Response getPartnerById(String partnerId) {
        return getHeader()
                .get(PARTNER_BY_ID_API_PATH.apply(partnerId))
                .thenReturn();
    }

    public Response getPartnerByClientId(String clientId) {
        return getHeader()
                .get(PARTNER_BY_CLIENT_ID_API_PATH.apply(clientId))
                .thenReturn();
    }

    public Response getPartnerByLocationId(String locationId) {
        return getHeader()
                .get(PARTNER_BY_LOCATION_ID_API_PATH.apply(locationId))
                .thenReturn();
    }

    public Response getpartnersList(String[] ids) {
        return getHeader()
                .body(ids)
                .post(PARTNERS_LIST_API_PATH)
                .thenReturn();
    }

    @Step("Create default partner")
    public PartnerCreateResponse createDefaultPartner(String partnerId, String partnerPassword, String partnerName,
            String locationName) {
        return createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.REAL_ESTATE)
                .clientId(partnerId)
                .clientSecret(partnerPassword)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(USE_GLOBAL_CURRENCY_RATE)
                .name(partnerName)
                .description(generateRandomString(20))
                .locations(new LocationCreateModel[]{
                        LocationCreateModel
                                .locationBuilder()
                                .name(locationName)
                                .address(FakerUtils.address)
                                .externalId(SOME_EXTERNAL_ID + locationName)
                                .accountingIntegrationCode(generateRandomString(80))
                                .contactPerson(
                                        ContactPersonModel
                                                .builder()
                                                .firstName(FakerUtils.firstName)
                                                .lastName(FakerUtils.lastName)
                                                .phoneNumber(FakerUtils.phoneNumber)
                                                .email(generateRandomEmail())
                                                .build()).build()})
                .useGlobalCurrencyRate(false)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);
    }

    public PartnerDto createPartner(String partnerId, String partnerPassword, String partnerName,
            String locationName) {
        val partnerCreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(partnerId)
                .clientSecret(partnerPassword)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(USE_GLOBAL_CURRENCY_RATE)
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
                        .externalId(locationName)
                        .name(partnerName)
                        .accountingIntegrationCode(generateRandomString(10))
                        .build()})
                .name(partnerName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        return PartnerDto
                .builder()
                .id(partnerCreationActualResult.getId())
                .name(partnerName)
                .clientId(partnerId)
                .locationId(getLocationId(partnerCreationActualResult))
                .build();
    }


    public PartnerDto createPartner(String name) {
        var partnerId = getRandomUuid();
        val partnerName = FakerUtils.companyName;
        val clientId = getRandomUuid();

        val partnerCreationActualResult = createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(clientId)
                .clientSecret(generateValidPassword())
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(USE_GLOBAL_CURRENCY_RATE)
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
                        .externalId(SOME_EXTERNAL_ID + generateRandomString(10))
                        .name(generateRandomString(10))
                        .build()})
                .name(partnerName)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerCreateResponse.class);

        return PartnerDto
                .builder()
                .id(partnerCreationActualResult.getId())
                .name(partnerName)
                .clientId(clientId)
                .locationId(getLocationId(partnerCreationActualResult))
                .build();
    }

    public Response createPartner(String partnerName, String clientId, String clientSecret) {
        var partnerId = getRandomUuid();

        return createPartner(PartnerCreateModel
                .partnerBuilder()
                .businessVertical(Vertical.HOSPITALITY)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .createdBy(getRandomUuid())
                .amountInTokens(SOME_AMOUNT_IN_TOKENS)
                .amountInCurrency(SOME_AMOUNT_IN_CURRENCY)
                .useGlobalCurrencyRate(USE_GLOBAL_CURRENCY_RATE)
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
                        .externalId(SOME_EXTERNAL_ID + generateRandomString(10))
                        .name(generateRandomString(10))
                        .build()})
                .name(partnerName)
                .build())
                .thenReturn();
    }

    @Step("Get partner location id")
    public String getLocationId(PartnerCreateResponse partnerData) {
        return getPartnerById(partnerData.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class).getLocations()[0].getId();
    }

    @Step("Get partner location id")
    public String getLocationId(PartnerDto partnerData) {
        return getPartnerById(partnerData.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class).getLocations()[0].getId();
    }

    @Step("Get partner external location id")
    public String getLocationExternalId(PartnerCreateResponse partnerData) {
        return getPartnerById(partnerData.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class).getLocations()[0].getExternalId();
    }

    @Step("Get partner external location id")
    public String getLocationExternalId(PartnerDto partnerData) {
        return getPartnerById(partnerData.getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerDetailsModel.class).getLocations()[0].getExternalId();
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class PartnerDto {

        private String id;
        private String name;
        private String clientId;
        private String locationId;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String[] clientId;
        private String[] clientSecret;
    }
}
