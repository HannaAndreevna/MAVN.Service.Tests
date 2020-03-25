package com.lykke.tests.api.service.agentmanagement;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.getAgent;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.getListOfAgents;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.postAgent;
import static com.lykke.tests.api.service.agentmanagement.model.AgentManagementErrorCode.COUNTRY_OF_RESIDENCE_DOES_NOT_EXIST;
import static com.lykke.tests.api.service.agentmanagement.model.AgentManagementErrorCode.EMAIL_NOT_VERIFIED;
import static com.lykke.tests.api.service.agentmanagement.model.DocumentType.PASSPORT;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_ACCOUNT_NUMBER_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_BANK_ADDRESS_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_BANK_BRANCH_COUNTRY_ID_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_BANK_BRANCH_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_BANK_NAME_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_BENEFICIARY_NAME_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.BANK_INFO_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.IMAGES_0_DOCUMENT_TYPE_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.IMAGES_FIELD;
import static com.lykke.tests.api.service.agentmanagement.model.ValidationErrorResponseModelEmulator.getErrorMessages;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.notificationsystembroker.EmailConfirmationUtils.confirmRegistration;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.ByCustomerIdRequestModel;
import com.lykke.tests.api.service.agentmanagement.model.AgentManagementErrorCode;
import com.lykke.tests.api.service.agentmanagement.model.AgentModel;
import com.lykke.tests.api.service.agentmanagement.model.BankInfoModel;
import com.lykke.tests.api.service.agentmanagement.model.ImageModel;
import com.lykke.tests.api.service.agentmanagement.model.RegistrationModel;
import com.lykke.tests.api.service.agentmanagement.model.RegistrationResultModel;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AgentManagementTests extends BaseApiTest {

    private static final int COUNTRY_OF_RESIDENCE_INVALID_ID = 2232;

    static Stream<Arguments> getInvalidRegistrationModel() {
        return Stream.of(
                of(RegistrationModel
                        .builder()
                        .customerId(getRandomUuid())
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneNumber(FakerUtils.phoneNumber)
                        .countryOfResidenceId(AgentManagementUtils.COUNTRY_OF_RESIDENCE_ID)
                        .countryPhoneCodeId(AgentManagementUtils.COUNTRY_PHONE_CODE_ID)
                        .build(), getErrorMessages(IMAGES_FIELD,
                        BANK_INFO_FIELD)),
                of(RegistrationModel
                        .builder()
                        .customerId(getRandomUuid())
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneNumber(FakerUtils.phoneNumber)
                        .bankInfo(BankInfoModel
                                .builder()
                                .build())
                        .images(new ImageModel[]{ImageModel
                                .builder()
                                .name(AgentManagementUtils.IMAGE_NAME)
                                .documentType(PASSPORT)
                                .content(AgentManagementUtils.IMAGE_CONTENT)
                                .build()})
                        .countryOfResidenceId(AgentManagementUtils.COUNTRY_OF_RESIDENCE_ID)
                        .countryPhoneCodeId(AgentManagementUtils.COUNTRY_PHONE_CODE_ID)
                        .build(), getErrorMessages(BANK_INFO_BANK_NAME_FIELD, BANK_INFO_ACCOUNT_NUMBER_FIELD,
                        BANK_INFO_BANK_ADDRESS_FIELD,
                        BANK_INFO_BANK_BRANCH_COUNTRY_ID_FIELD,
                        BANK_INFO_BANK_BRANCH_FIELD,
                        BANK_INFO_BENEFICIARY_NAME_FIELD
                )),
                of(RegistrationModel
                        .builder()
                        .customerId(getRandomUuid())
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneNumber(FakerUtils.phoneNumber)
                        .bankInfo(BankInfoModel
                                .builder()
                                .accountNumber(AgentManagementUtils.ACCOUNT_NUMBER)
                                .bankAddress(AgentManagementUtils.BANK_ADDRESS)
                                .bankBranch(AgentManagementUtils.BANK_BRANCH)
                                .bankBranchCountryId(1)
                                .bankName(AgentManagementUtils.BANK_NAME)
                                .beneficiaryName(AgentManagementUtils.BENEFICIARY_NAME)
                                .iban(AgentManagementUtils.IBAN)
                                .build())
                        .images(new ImageModel[]{ImageModel
                                .builder()
                                .build()})
                        .countryOfResidenceId(AgentManagementUtils.COUNTRY_OF_RESIDENCE_ID)
                        .countryPhoneCodeId(AgentManagementUtils.COUNTRY_PHONE_CODE_ID)
                        .build(), getErrorMessages(IMAGES_0_DOCUMENT_TYPE_FIELD
                ))
        );
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1334)
    void shouldGetCustomerInfoForNonVerifiedEmail() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        confirmRegistration(user.getEmail(), user.getPassword());

        getAgent(ByCustomerIdRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(1334)
    void shouldGetCustomerInfoForVerifiedEmail() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        confirmRegistration(user.getEmail(), user.getPassword());

        getAgent(ByCustomerIdRequestModel
                .builder()
                .customerId(customerId)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(storyId = {1334, 1780})
    void shouldNotPostCustomerInfoIfEmailIsNotVerified() {
        var user = new RegistrationRequestModel();
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, false);

        val actualResult = postAgent(RegistrationModel
                .builder()
                .customerId(customerData.getCustomerId())
                .firstName(customerData.getFirstName())
                .lastName(customerData.getLastName())
                .phoneNumber(customerData.getPhoneNumber())
                .bankInfo(BankInfoModel
                        .builder()
                        .accountNumber(AgentManagementUtils.ACCOUNT_NUMBER)
                        .bankAddress(AgentManagementUtils.BANK_ADDRESS)
                        .bankBranch(AgentManagementUtils.BANK_BRANCH)
                        .bankBranchCountryId(1)
                        .bankName(AgentManagementUtils.BANK_NAME)
                        .beneficiaryName(AgentManagementUtils.BENEFICIARY_NAME)
                        .iban(AgentManagementUtils.IBAN)
                        .build())
                .images(new ImageModel[]{ImageModel
                        .builder()
                        .name(AgentManagementUtils.IMAGE_NAME)
                        .documentType(PASSPORT)
                        .content(AgentManagementUtils.IMAGE_CONTENT)
                        .build()})
                .countryOfResidenceId(AgentManagementUtils.COUNTRY_OF_RESIDENCE_ID)
                .countryPhoneCodeId(AgentManagementUtils.COUNTRY_PHONE_CODE_ID)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResultModel.class);

        assertAll(
                () -> assertEquals(EMAIL_NOT_VERIFIED, actualResult.getErrorCode())
        );
    }

    @Test
    @UserStoryId(storyId = {1334, 1780})
    void shouldPostCustomerInfoIfEmailIsVerified() {
        val customerData = registerDefaultVerifiedCustomer();

        val actualResult = postAgent(RegistrationModel
                .builder()
                .customerId(customerData.getCustomerId())
                .firstName(customerData.getFirstName())
                .lastName(customerData.getLastName())
                .phoneNumber(customerData.getPhoneNumber())
                .bankInfo(BankInfoModel
                        .builder()
                        .accountNumber(AgentManagementUtils.ACCOUNT_NUMBER)
                        .bankAddress(AgentManagementUtils.BANK_ADDRESS)
                        .bankBranch(AgentManagementUtils.BANK_BRANCH)
                        .bankBranchCountryId(1)
                        .bankName(AgentManagementUtils.BANK_NAME)
                        .beneficiaryName(AgentManagementUtils.BENEFICIARY_NAME)
                        .iban(AgentManagementUtils.IBAN)
                        .build())
                .images(new ImageModel[]{ImageModel
                        .builder()
                        .name(AgentManagementUtils.IMAGE_NAME)
                        .documentType(PASSPORT)
                        .content(AgentManagementUtils.IMAGE_CONTENT)
                        .build()})
                .countryOfResidenceId(AgentManagementUtils.COUNTRY_OF_RESIDENCE_ID)
                .countryPhoneCodeId(AgentManagementUtils.COUNTRY_PHONE_CODE_ID)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResultModel.class);

        // depending on campaigns available error code could be None or NotEnoughTokens
        assertTrue(Stream
                .of(AgentManagementErrorCode.NONE, AgentManagementErrorCode.NOT_ENOUGH_TOKENS)
                .collect(toList())
                .contains(actualResult.getErrorCode()));
    }

    @ParameterizedTest
    @MethodSource("getInvalidRegistrationModel")
    @UserStoryId(1780)
    void shouldPostCustomerInfoIfEmailIsVerified(
            RegistrationModel requestObject,
            Map<String, String> expectedErrorMessages) {
        Map<String, ArrayList<String>> actualResult = postAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .jsonPath()
                .getMap(EMPTY);

        actualResult
                .entrySet()
                .stream()
                .forEach(item -> assertTrue(expectedErrorMessages.values().contains(item.getValue().get(0))));
    }

    @Test
    @UserStoryId(1901)
        // TODO: do more validation
    void shouldNotPostCustomerInfoIfCountryOfResidenceIsInvalid() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        confirmRegistration(user.getEmail(), user.getPassword());

        val firstName = FakerUtils.firstName;
        val lastName = FakerUtils.lastName;
        val phoneNumber = FakerUtils.phoneNumber;

        val actualResult = postAgent(RegistrationModel
                .builder()
                .customerId(customerId)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .bankInfo(BankInfoModel
                        .builder()
                        .accountNumber(AgentManagementUtils.ACCOUNT_NUMBER)
                        .bankAddress(AgentManagementUtils.BANK_ADDRESS)
                        .bankBranch(AgentManagementUtils.BANK_BRANCH)
                        .bankBranchCountryId(1)
                        .bankName(AgentManagementUtils.BANK_NAME)
                        .beneficiaryName(AgentManagementUtils.BENEFICIARY_NAME)
                        .iban(AgentManagementUtils.IBAN)
                        .build())
                .images(new ImageModel[]{ImageModel
                        .builder()
                        .name(AgentManagementUtils.IMAGE_NAME)
                        .documentType(PASSPORT)
                        .content(AgentManagementUtils.IMAGE_CONTENT)
                        .build()})
                .countryOfResidenceId(COUNTRY_OF_RESIDENCE_INVALID_ID)
                .countryPhoneCodeId(AgentManagementUtils.COUNTRY_PHONE_CODE_ID)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResultModel.class);

        assertEquals(COUNTRY_OF_RESIDENCE_DOES_NOT_EXIST, actualResult.getErrorCode());
    }

    @Test
    @UserStoryId(2560)
    void shouldGetAgentListByCustomersIds() {
        val numberOfAgents = 3;
        val expectedResult = IntStream.range(0, numberOfAgents)
                .mapToObj(index -> AgentManagementUtils.registerDefaultAgent().getCustomerId())
                .sorted()
                .collect(toList())
                .toArray(new String[]{});

        Awaitility.await()
                .atMost(Duration.ONE_MINUTE)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> numberOfAgents == getListOfAgents(expectedResult)
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract()
                        .as(AgentModel[].class)
                        .length);

        val actualAgents = getListOfAgents(expectedResult)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AgentModel[].class);
        val actualResult = Arrays.stream(actualAgents)
                .map(agent -> agent.getCustomerId().toString())
                .sorted()
                .collect(toList())
                .toArray(new String[]{});

        assertArrayEquals(expectedResult, actualResult);
    }

}
