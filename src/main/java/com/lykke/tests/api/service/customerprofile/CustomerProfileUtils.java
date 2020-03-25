package com.lykke.tests.api.service.customerprofile;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.base.RequestHeader.getHeaderWithKey;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomPhone;
import static com.lykke.api.testing.api.common.JsonConversionUtils.convertToJson;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.CustomerProfileApiEndpoint.PAGINATED_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_API_CUSTOMERS_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_API_STATISTICS_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_BY_PHONE_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_CUSTOMERS_IDS_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PROFILE_SET_EMAIL_AS_VERIFIED_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.API_KEY;
import static com.lykke.tests.api.common.CommonConsts.CustomerData.PHONE_NUMBER_LENGTH;
import static com.lykke.tests.api.common.CommonConsts.RegistrationData.COUNTRY_OF_NATIONALITY_ID_01;
import static com.lykke.tests.api.service.customermanagement.model.register.LoginProvider.STANDARD;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static java.util.stream.Collectors.toMap;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfile;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileQueryParams;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileRequestModel;
import com.lykke.tests.api.service.customerprofile.model.CustomerProfileUpdateRequestModel;
import com.lykke.tests.api.service.customerprofile.model.EncryptionKey;
import com.lykke.tests.api.service.customerprofile.model.LoginProvider;
import com.lykke.tests.api.service.customerprofile.statistics.model.CustomerStatiscticsRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.IntStream;
import junit.framework.AssertionFailedError;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

@UtilityClass
public class CustomerProfileUtils {

    private static final String REGISTRATIONS_COUNT = "RegistrationsCount";
    private static final String TOTAL_COUNT = "TotalCount";

    @SneakyThrows
    public Response postCustomerProfileKey(String token, String key) {
        return getHeader(token)
                .body(convertToJson(
                        EncryptionKey
                                .builder()
                                .key(key)
                                .build()))
                .post(CUSTOMER_PROFILE_API_PATH);
    }

    @Step("Create customer profile")
    public Response createCustomerProfile(String key, String customerId, String email, String firstName,
            String lastName, LoginProvider loginProvider) {
        validateCustomerId(customerId);
        return getHeaderWithKey(key)
                .body(createCustomerProfileObject(
                        customerId, email, firstName, lastName, loginProvider))
                .post(CUSTOMER_PROFILE_API_CUSTOMERS_PATH);
    }

    @Step("Create customer profile")
    public Response createCustomerProfile(CustomerProfileRequestModel customerProfileRequest) {
        validateCustomerId(customerProfileRequest.getCustomerId());
        return getHeaderWithKey(API_KEY)
                .body(customerProfileRequest)
                .post(CUSTOMER_PROFILE_API_CUSTOMERS_PATH);
    }

    @Step("Get customer profile by email {email}")
    public Response getCustomerProfileByEmail(String email) {
        return getHeaderWithKey(API_KEY)
                .queryParam("email", email)
                .get(CUSTOMER_PROFILE_API_CUSTOMERS_PATH)
                .thenReturn();
    }

    public Response getCustomerProfile(String key, String customerId) {
        return getHeaderWithKey(key).queryParam("includeNotVerified", true)
                .get(CUSTOMER_PROFILE_BY_ID_API_PATH.apply(customerId));
    }

    public Response getCustomerProfileByPhone(String phone) {
        return getHeaderWithKey(API_KEY)
                .get(CUSTOMER_PROFILE_BY_PHONE_API_PATH.apply(phone));
    }

    public Response deleteCustomerProfile(String key, String customerId) {
        return getHeaderWithKey(key)
                .delete(CUSTOMER_PROFILE_BY_ID_API_PATH.apply(customerId));
    }

    public static Response updateCustomerProfile(CustomerProfileUpdateRequestModel requestObject) {
        validateCustomerId(requestObject.getCustomerId());
        return getHeaderWithKey(API_KEY)
                .body(requestObject)
                .put(CUSTOMER_PROFILE_API_CUSTOMERS_PATH)
                .thenReturn();
    }

    public static String createCustomerProfileWithPhoneNumber(String email) {
        return createCustomerProfileWithPhoneNumber(email, "(922) 673-0854 x9844");
    }

    public static String createCustomerProfileWithPhoneNumber(String email, String phone) {
        return createCustomerProfileWithPhoneNumber(email, phone, generateValidPassword());
    }

    public static String createCustomerProfileWithPhoneNumber(String email, String phone, String password) {
        String customerWithPhoneId = registerCustomer(RegistrationRequestModel
                .completeCustomerBuilder()
                .email(email)
                .password(password)
                .phoneNumber(generateRandomPhone(PHONE_NUMBER_LENGTH))
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .loginProvider(STANDARD).build());
        updateCustomerProfile(
                CustomerProfileUpdateRequestModel
                        .builder()
                        .customerId(customerWithPhoneId)
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneNumber(phone)
                        .countryPhoneCodeId(1)
                        .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);
        return customerWithPhoneId;
    }

    public Response getPagination(String key, int currentPage, int pageSize) {
        return getHeaderWithKey(key)
                .given()
                .queryParams(
                        getQueryParams(CustomerProfileQueryParams
                                .builder()
                                .currentPage(currentPage)
                                .pageSize(pageSize)
                                .build()))
                .get(CUSTOMER_PROFILE_API_CUSTOMERS_PATH + PAGINATED_PATH.getPath());
    }

    Response createCustomerProfileWithoutApiKey(String token, String customerId, String email, String firstName,
            String lastName, LoginProvider loginProvider) {
        validateCustomerId(customerId);
        return getHeader(token)
                .body(createCustomerProfileObject(
                        customerId, email, firstName, lastName, loginProvider))
                .post(CUSTOMER_PROFILE_API_CUSTOMERS_PATH);
    }

    Response getCustomerProfileWithoutApiKey(String token, String customerId) {
        return getHeader(token)
                .get(CUSTOMER_PROFILE_BY_ID_API_PATH.apply(customerId));
    }

    Response deleteCustomerProfileWithoutApiKey(String token, String customerId) {
        return getHeader(token)
                .delete(CUSTOMER_PROFILE_BY_ID_API_PATH.apply(customerId));
    }

    Response getPaginationWithoutApiKey(String token, int currentPage, int pageSize) {
        return getHeader(token)
                .given()
                .queryParams(getQueryParams(CustomerProfileQueryParams
                        .builder()
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .build()))
                .get(CUSTOMER_PROFILE_API_CUSTOMERS_PATH + PAGINATED_PATH.getPath());
    }

    CustomerProfile[] getCustomerProfilesByIds(Map<String, String> customersData) {
        return getHeaderWithKey(API_KEY)
                .body(customersData.keySet().stream().map(key -> customersData.get(key)))
                .post(CUSTOMER_PROFILE_CUSTOMERS_IDS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerProfile[].class);
    }

    Response setCustomerEmailAsVerified(String customerId) {
        return getHeaderWithKey(API_KEY)
                .put(CUSTOMER_PROFILE_SET_EMAIL_AS_VERIFIED_API_PATH.apply(customerId));
    }

    public int getRegistrationCount(String startDate, String endDate) {
        return getStatistics(startDate, endDate)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(REGISTRATIONS_COUNT);
    }

    public int getTotalCount(String startDate, String endDate) {
        return getStatistics(startDate, endDate)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(TOTAL_COUNT);
    }

    Response getStatistics(String startDate, String endDate) {
        return getHeaderWithKey(API_KEY)
                .queryParams(getQueryParams(CustomerStatiscticsRequest
                        .builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build()))
                .get(CUSTOMER_PROFILE_API_STATISTICS_PATH);
    }

    Map<String, String> generateNumberOfCustomerProfiles(int number) {
        val customersData = IntStream.range(0, number)
                .mapToObj(i -> registerDefaultVerifiedCustomer())
                .collect(toMap(x -> x.getEmail(),
                        x -> x.getCustomerId()));

        customersData.keySet()
                .forEach(customerData ->
                        createCustomerProfile(
                                API_KEY,
                                customersData.get(customerData),
                                customerData,
                                FakerUtils.firstName,
                                FakerUtils.lastName,
                                LoginProvider.STANDARD));
        // (email, id) pairs
        return customersData;
    }

    private CustomerProfileRequestModel createCustomerProfileObject(String customerId, String email, String firstName,
            String lastName, LoginProvider loginProvider) {
        return CustomerProfileRequestModel
                .builder()
                .customerId(customerId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .loginProvider(loginProvider)
                .countryOfNationalityId(COUNTRY_OF_NATIONALITY_ID_01)
                .build();
    }

    int getCustomersCount(String key) {
        var customerCount = 0;
        ArrayList da;
        var currentPage = 1;
        val pageSize = 1000;

        do {
            da = getPagination(key, currentPage, pageSize)
                    .then()
                    .statusCode(SC_OK)
                    .extract()
                    .path("Customers");

            currentPage++;
            customerCount += da.size();
        } while (da.size() != 0);

        return customerCount;
    }

    private void validateCustomerId(String customerId) {
        if (customerId.split("-").length != 5) {
            throw new AssertionFailedError(String.format("Invalid customerId %s. Should be UUID string", customerId));
        }
    }
}
