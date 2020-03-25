package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customer.CustomerInfoUtils.getCurrentCustomerInfo;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.RegisterCustomerUtils.registerUser;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customer.model.CustomerInfoResponseModel;
import com.lykke.tests.api.service.customer.model.RegistrationRequestModel;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CurrentCustomerInfoTests extends BaseApiTest {

    private static final String EMAIL_FIELD = "Email";
    private static final String FIRST_NAME_FIELD = "FirstName";
    private static final String LAST_NAME_FIELD = "LastName";
    private static final String PHONE_NUMBER_FIELD = "PhoneNumber";
    private static final String COUNTRY_PHONE_CODE_ID = "CountryPhoneCodeId";

    RegistrationRequestModel user;

    @BeforeEach
    void beforeEach() {
        user = new RegistrationRequestModel();
        registerUser(user);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {474, 2621, 3768})
    void shouldGetCurrentCustomerInfo() {
        val actualResult = getCurrentCustomerInfo(getUserToken(user))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(EMAIL_FIELD, equalTo(user.getEmail()))
                .body(FIRST_NAME_FIELD, equalTo(user.getFirstName()))
                .body(LAST_NAME_FIELD, equalTo(user.getLastName()))
                .extract()
                .as(CustomerInfoResponseModel.class);

        assertAll(
                () -> assertEquals(user.getEmail(), actualResult.getEmail()),
                () -> assertEquals(user.getFirstName(), actualResult.getFirstName()),
                () -> assertEquals(user.getLastName(), actualResult.getLastName()),
                () -> assertEquals(user.getCountryOfNationalityId(), actualResult.getCountryOfNationalityId())
        );
    }

    @Test
    @UserStoryId(storyId = {3409, 3768})
    void shouldContainCountryPhoneId() {
        var customer = registerDefaultVerifiedCustomer();
        val actualResult = getCurrentCustomerInfo(getUserToken(customer.getEmail(), customer.getPassword()))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(EMAIL_FIELD, equalTo(customer.getEmail()))
                .body(FIRST_NAME_FIELD, equalTo(customer.getFirstName()))
                .body(LAST_NAME_FIELD, equalTo(customer.getLastName()))
                .body(COUNTRY_PHONE_CODE_ID, equalTo(1))
                .extract()
                .as(CustomerInfoResponseModel.class);

        assertAll(
                () -> assertEquals(customer.getEmail(), actualResult.getEmail()),
                () -> assertEquals(customer.getFirstName(), actualResult.getFirstName()),
                () -> assertEquals(customer.getLastName(), actualResult.getLastName()),
                () -> assertEquals(customer.getCountryOfNationalityId(), actualResult.getCountryOfNationalityId()),
                () -> assertEquals(customer.getPhoneNumber(), actualResult.getPhoneNumber())
        );
    }
}
