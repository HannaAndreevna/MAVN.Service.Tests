package com.lykke.tests.api.service.agentmanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.getRequirements;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.getRequirementsTokens;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.updateRequirementsTokens;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.notificationsystembroker.EmailConfirmationUtils.confirmRegistration;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.ByCustomerIdRequestModel;
import com.lykke.tests.api.service.agentmanagement.model.CustomerRequirementsModel;
import com.lykke.tests.api.service.agentmanagement.model.TokensRequirementModel;
import com.lykke.tests.api.service.agentmanagement.model.UpdateTokensRequirementModel;
import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.Random;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class RequirementsTests extends BaseApiTest {

    //   @Disabled("TODO: needs investigation")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1334)
    void shouldGetRequirementsForNonVerifiedEmail() {
        val customerId = registerCustomer();
        val actualResult = getRequirements(ByCustomerIdRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()

                // TODO: error 500
                .log().all()
////xx
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerRequirementsModel.class);

        assertAll(
                () -> assertEquals(customerId, actualResult.getCustomerId()),
                () -> assertEquals(false, actualResult.isEligible()),
                () -> assertEquals(false, actualResult.hasVerifiedEmail())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1334)
    void shouldGetRequirementsForVerifiedEmail() {
        val customerData = RegisterCustomerUtils.registerDefaultVerifiedCustomer();

        val actualResult = getRequirements(ByCustomerIdRequestModel
                .builder()
                .customerId(customerData.getCustomerId())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerRequirementsModel.class);

        assertAll(
                () -> assertEquals(customerData.getCustomerId(), actualResult.getCustomerId()),
                () -> assertEquals(false, actualResult.isEligible()),
                () -> assertEquals(true, actualResult.hasVerifiedEmail())
        );
    }

    @Disabled("TODO: needs investigation")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1334)
    void shouldGetRequirementsForPartiallyVerifiedEmail() {
        val email = generateRandomEmail();
        val password = generateValidPassword();
        var user = new RegistrationRequestModel();
        user.setPassword(password);
        user.setEmail(email);
        val customerId = registerCustomer(user);
        confirmRegistration(email, password);

        val actualResult = getRequirements(ByCustomerIdRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()

                // TODO: error 500
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(CustomerRequirementsModel.class);

        assertAll(
                () -> assertEquals(customerId, actualResult.getCustomerId()),
                () -> assertEquals(false, actualResult.isEligible()),
                () -> assertEquals(true, actualResult.hasVerifiedEmail())
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1334)
    void shouldGetRequirementsTokensForNonVerifiedEmail() {
        val actualResult = getRequirementsTokens()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TokensRequirementModel.class);

        assertNotEquals("0", actualResult.getRequiredNumberOfTokens());
    }

    @Test
    @UserStoryId(2650)
    void shouldUpdateRequirementsTokensForNonVerifiedEmail() {
        val currentRequirements = getRequirementsTokens()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TokensRequirementModel.class);

        val currentAmount = Double.valueOf(currentRequirements.getRequiredNumberOfTokens());
        final Double newAmount = currentAmount + new Random(10).nextDouble();

        updateRequirementsTokens(UpdateTokensRequirementModel
                .builder()
                .amount(newAmount.toString())
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK);

        val actualResult = getRequirementsTokens()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TokensRequirementModel.class);

        assertEquals(newAmount.toString(), actualResult.getRequiredNumberOfTokens());
    }
}
