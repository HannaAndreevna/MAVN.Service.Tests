package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.base.Paths.Customer.GOOGLE_REGISTER_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.REGISTER_CUSTOMER_API_PATH;
import static com.lykke.tests.api.base.Paths.CustomerProfile.CUSTOMER_PHONES_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.RegistrationData.COUNTRY_OF_NATIONALITY_ID_01;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.customer.model.GoogleRegistrationRequestModel;
import com.lykke.tests.api.service.customer.model.RegistrationRequestModel;
import com.lykke.tests.api.service.customer.model.SetCustomerPhoneInfoRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class RegisterCustomerUtils {

    public static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static final String TOKEN_FIELD = "Token";

    public static String registerUser() {
        return registerUser(generateRandomEmail(), generateValidPassword());
    }

    @Step("Register customer")
    public String registerUser(RegistrationRequestModel requestModel) {
        return registerUserWithEmailAndPassword(requestModel.getEmail(), requestModel.getPassword())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_ID_FIELD);
    }

    @Step("Register customer with email: '{emailAddress}' and password: '{password}'")
    public String registerUser(String emailAddress, String password) {
        return registerUserWithEmailAndPassword(emailAddress, password)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path(CUSTOMER_ID_FIELD);
    }

    @Step("Register customer with email: '{emailAddress}' and password: '{password}'")
    Response registerUserWithEmailAndPassword(String emailAddress, String password) {
        return getHeader()
                .body(userRegisterObject(emailAddress, password))
                .post(REGISTER_CUSTOMER_API_PATH);
    }

    public String registerDefaultCustomer(String emailAddress, String password) {
        val customerId = registerUser(emailAddress, password);

        getHeader()
                .body(SetCustomerPhoneInfoRequestModel
                        .builder()
                        .customerId(customerId)
                        .phoneNumber(FakerUtils.phoneNumber)
                        .countryPhoneCodeId(1)
                        .build())
                .post(CUSTOMER_PHONES_API_PATH);

        return customerId;
    }

    public Response registerWithGoogle(GoogleRegistrationRequestModel googleRegistrationRequest) {
        return getHeader()
                .body(googleRegistrationRequest)
                .post(GOOGLE_REGISTER_API_PATH);
    }

    private static RegistrationRequestModel userRegisterObject(String emailAddress, String password) {
        return RegistrationRequestModel
                .builder()
                .email(emailAddress)
                .password(password)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .countryOfNationalityId(COUNTRY_OF_NATIONALITY_ID_01)
                .build();
    }
}
