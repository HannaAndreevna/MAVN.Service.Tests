package com.lykke.tests.api.service.partnermanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createDefaultPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.loginPartner;
import static com.lykke.tests.api.service.partnermanagement.model.PartnerManagementError.LOGIN_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.partnermanagement.model.PartnerCreateResponse;
import com.lykke.tests.api.service.partnermanagement.model.auth.AuthenticateRequestModel;
import com.lykke.tests.api.service.partnermanagement.model.auth.AuthenticateResponseModel;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class AuthTests extends BaseApiTest {

    private static final String SOME_USER_INFO = "user info 1";
    private String partnerId;
    private String partnerPassword;
    private String partnerToken;
    private String customerId;
    private String customerPassword;
    private String customerToken;
    private String email;
    private String phone;
    private String locationId;
    private PartnerCreateResponse partnerData;

    @BeforeEach
    void setUp() {
        partnerPassword = generateValidPassword();
        email = generateRandomEmail();
        phone = FakerUtils.phoneNumber;
        customerPassword = generateValidPassword();

        partnerId = getRandomUuid();
        partnerData = createDefaultPartner(partnerId, partnerPassword, generateRandomString(10),
                generateRandomString(10));
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(partnerId, partnerPassword, USER_INFO);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1288)
    void shouldNotLoginNonExistingPartner() {
        val expectedResult = AuthenticateResponseModel
                .builder()
                .error(LOGIN_NOT_FOUND)
                .build();
        val actualResult = loginPartner(AuthenticateRequestModel
                .builder()
                .clientId(partnerData.getId())
                .clientSecret(partnerPassword)
                .userInfo(SOME_USER_INFO)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AuthenticateResponseModel.class);

        assertEquals(expectedResult, actualResult);
    }
}
