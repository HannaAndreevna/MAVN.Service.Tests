package com.lykke.tests.api.service.privateblockchainfacade;

import static com.lykke.api.testing.api.common.FakerUtils.firstName;
import static com.lykke.api.testing.api.common.FakerUtils.lastName;
import static com.lykke.api.testing.api.common.FakerUtils.phoneNumber;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.RabbitMqTemporaryConsts.MAVNPropertyIntegration.getRabbitMqRequestData;
import static com.lykke.tests.api.common.RabbitMqTemporaryConsts.MAVNPropertyIntegration.getRabbitMqResponseData;
import static com.lykke.api.testing.api.common.RabbitMqUtils.receiveMessage;
import static com.lykke.api.testing.api.common.RabbitMqUtils.sendMessage;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.model.rabbitmq.AgentRegisterRequestEvent;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AgentRegistrationTests extends BaseApiTest {

    static Stream<Arguments> getInvalidCustomerData() {
        return Stream.of(
                of(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)
        );
    }

    @Test
    @UserStoryId(storyId = {1336, 1588})
    void shouldRegisterAgent() {
        var user = new RegistrationRequestModel();
        val email = user.getEmail();
        val customerId = registerCustomer(user);
        val message = AgentRegisterRequestEvent
                .builder()
                .customerId(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
        sendMessage(getRabbitMqRequestData(), message);
    }

    @ParameterizedTest
    @MethodSource("getInvalidCustomerData")
    @UserStoryId(storyId = {1336, 1588})
    void shouldRegisterAgent(String customerId, String firstName, String lastName, String email, String phoneNumber) {
        val message = AgentRegisterRequestEvent
                .builder()
                .customerId(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
        sendMessage(getRabbitMqRequestData(), message);
    }

    ////33  @Disabled("unable to catch the response message")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1336, 1588})
    void shouldGetResponseOnAgentRegistration() {
        var user = new RegistrationRequestModel();
        val email = user.getEmail();
        val customerId = registerCustomer(user);
        val message = AgentRegisterRequestEvent
                .builder()
                .customerId(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
        sendMessage(getRabbitMqRequestData(), message);
        receiveMessage(getRabbitMqResponseData(), AgentRegisterRequestEvent.class);
    }
}
