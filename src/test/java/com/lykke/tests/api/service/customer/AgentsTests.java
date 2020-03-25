package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.ALREADY_REGISTERED_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonConsts.CONTENT_IS_NOT_BASE64_MSG;
import static com.lykke.tests.api.common.CommonConsts.CONTENT_IS_REQUIRED_MSG;
import static com.lykke.tests.api.common.CommonConsts.DEFAULT_CURRENCY;
import static com.lykke.tests.api.common.CommonConsts.EMAIL_NOT_VERIFIED_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.ERROR_CODE_NONE;
import static com.lykke.tests.api.common.CommonConsts.ERROR_FIELD;
import static com.lykke.tests.api.common.CommonConsts.IBAN_INVALID_MSG;
import static com.lykke.tests.api.common.CommonConsts.IMAGE_IS_REQUIRED_MSG;
import static com.lykke.tests.api.common.CommonConsts.IMAGE_SIZE_VALIDATION_MSG;
import static com.lykke.tests.api.common.CommonConsts.MESSAGE_FIELD;
import static com.lykke.tests.api.common.CommonConsts.MODEL_VALIDATION_FAILURE;
import static com.lykke.tests.api.common.CommonConsts.NAME_REQUIRED_MSG;
import static com.lykke.tests.api.common.CommonConsts.NAME_VALIDATION_MSG;
import static com.lykke.tests.api.common.CommonConsts.NOTE_VALIDATION_MSG;
import static com.lykke.tests.api.common.CommonConsts.NO_TOKENS_ERR_MSG;
import static com.lykke.tests.api.common.CommonConsts.SWIFT_INVALID_MSG;
import static com.lykke.tests.api.common.prerequisites.Agents.agentRequestObject;
import static com.lykke.tests.api.common.prerequisites.Agents.createAnAgent;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.customer.AgentsUtils.getAgents;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.notificationsystembroker.EmailConfirmationUtils.confirmRegistration;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static com.lykke.tests.api.service.walletmanagement.WalletManagementUtils.balanceTransfer;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.Base64Utils;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.HelperUtils;
import com.lykke.tests.api.common.enums.DocumentType;
import com.lykke.tests.api.common.prerequisites.EarnRules;
import com.lykke.tests.api.service.customer.model.agents.AgentRegistrationRequestModel;
import com.lykke.tests.api.service.customer.model.agents.AgentsRequest;
import com.lykke.tests.api.service.customer.model.agents.BankInfoModel;
import com.lykke.tests.api.service.customer.model.agents.ImageModel;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
public class AgentsTests extends BaseApiTest {

    private static final String TEST_NAME = "Test";
    private static final String STATUS = "PendingKya";
    private static final int REQUIRED_NUMBER_OF_TOKENS = 100;
    private static final String content = Base64Utils.encodeToString(HelperUtils
            .getImagePath("test_image.jpg"));
    private static final String bigImage = Base64Utils.encodeToString(HelperUtils
            .getImagePath("14MB_image.jpg"));
    private static String earnRuleId;
    private BankInfoModel.BankInfoModelBuilder baseBankInfoObj;
    private ImageModel.ImageModelBuilder baseImagesObj;
    private AgentRegistrationRequestModel
            .AgentRegistrationRequestModelBuilder baseAgentRequestObject;

    private static Stream<Arguments> getWrongInputValues() {
        return Stream.of(
                of(EMPTY, TEST_NAME, generateRandomString()),
                of(TEST_NAME, EMPTY, generateRandomString()),
                of(TEST_NAME, TEST_NAME, EMPTY)
        );
    }

    @BeforeAll
    static void createSignUpEarnRule() {
        earnRuleId = EarnRules.createBasicSignUpEarnRule();
    }

    @AfterAll
    static void cleanUp() {
        deleteCampaign(earnRuleId);
    }

    @BeforeEach
    void setup() {
        baseBankInfoObj = BankInfoModel
                .builder()
                .beneficiaryName(FakerUtils.fullName)
                .bankName(FakerUtils.companyName)
                .bankBranch(FakerUtils.city)
                .accountNumber(FakerUtils.phoneNumber)
                .bankAddress(FakerUtils.address)
                .bankBranchCountryId(1)
                .iban("HU42117730161111101800000000")
                .swift("DABAIE2D");

        baseImagesObj = ImageModel
                .builder()
                .documentType(DocumentType.PASSPORT.getValue())
                .name(FakerUtils.title)
                .content(content);

        baseAgentRequestObject = AgentRegistrationRequestModel
                .builder()
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .countryPhoneCodeId(1)
                .countryOfResidenceId(1)
                .note(FakerUtils.randomQuote)
                .bankInfo(baseBankInfoObj.build())
                .images(new ImageModel[]{baseImagesObj.build()});
    }

    @Disabled("Update fields' validations")
    @ParameterizedTest(name = "Run {index}: firstName={0}, lastName={1}, phoneNumber={2}")
    @MethodSource("getWrongInputValues")
    @UserStoryId(storyId = 1333)
    void shouldNotCreateAgent_InvalidInput(String firstName, String lastName, String phoneNumber) {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        val customerToken = getUserToken(user.getEmail(), user.getPassword());

        val requestObject = AgentsRequest
                .builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .token(customerToken)
                .build();

        val createAgentErrorResponse = AgentsUtils.createAgentErrorResponse(requestObject);

        assertAll(
                () -> assertEquals(requestObject.getValidationErrorResponse(), createAgentErrorResponse)
        );
    }

    @Test
    @UserStoryId(storyId = 1333)
    void shouldCreateAndGetAgent() {
        val customerEmail = generateRandomEmail();
        val customerPass = generateValidPassword();
        val customerToken = createAnAgent(customerEmail, customerPass);

        val expectedResult = agentRequestObject.build();

        val actualResult = getAgents(customerToken);

        assertAll(
                () -> assertEquals(customerEmail, actualResult.getEmail()),
                () -> assertEquals(expectedResult.getFirstName(), actualResult.getFirstName()),
                () -> assertEquals(expectedResult.getLastName(), actualResult.getLastName()),
                () -> assertEquals("+91 " + expectedResult.getPhoneNumber(), actualResult.getPhoneNumber()),
                () -> assertEquals(STATUS, actualResult.getStatus()),
                () -> assertTrue(actualResult.getHasEnoughTokens()),
                () -> assertTrue(actualResult.getHasVerifiedEmail()),
                () -> assertEquals(REQUIRED_NUMBER_OF_TOKENS, actualResult.getRequiredNumberOfTokens())
        );
    }

    @Test
    @UserStoryId(storyId = 1333)
    void shouldNotGetAgent_Unauthorized() {

        val requestObject = agentRequestObject
                .token(getRandomUuid())
                .build();
        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @UserStoryId(storyId = 1333)
    void shouldNotCreate_SameAgent() {
        val customerEmail = generateRandomEmail();
        val customerPass = generateValidPassword();
        val customerToken = createAnAgent(customerEmail, customerPass);

        val requestObject = agentRequestObject
                .token(customerToken)
                .build();

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .body(MESSAGE_FIELD, equalTo(ALREADY_REGISTERED_ERR_MSG))
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(storyId = 1333)
    void shouldNotCreate_NotVerifiedEmail() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        val customerToken = getUserToken(user.getEmail(), user.getPassword());

        val requestObject = agentRequestObject
                .token(customerToken)
                .build();

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .body(MESSAGE_FIELD, equalTo(EMAIL_NOT_VERIFIED_ERR_MSG))
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(storyId = 1333)
    void shouldNotCreate_NotEnoughTokens() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        val receiverId = registerCustomer(new RegistrationRequestModel());

        waitForCustomerBalanceUpdate(customerId);

        val customerToken = getUserToken(user.getEmail(), user.getPassword());

        balanceTransfer(customerId, receiverId, DEFAULT_CURRENCY,
                Double.valueOf(getCustomerBalance(customerId)), getRandomUuid())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(ERROR_CODE_FIELD, equalTo(ERROR_CODE_NONE));

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Customer's balance: " + getCustomerBalance(customerId));
                    log.info("===============================================================================");
                    return 0 == Double.valueOf(getCustomerBalance(customerId));
                });

        val requestObject = agentRequestObject
                .token(customerToken)
                .build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .body(MESSAGE_FIELD, equalTo(NO_TOKENS_ERR_MSG))
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(storyId = 1554)
    void shouldNotCreateAgentWhenNoteLengthIsAbove2000() {
        val requestObject = baseAgentRequestObject.note(generateRandomString(2001)).build();

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(NOTE_VALIDATION_MSG));
    }

    @Test
    @UserStoryId(storyId = 1554)
    void shouldCreateAgentWhenNoteLengthIsLessThan2000() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);
        val customerToken = getUserToken(user.getEmail(), user.getPassword());

        waitForCustomerBalanceUpdate(customerId);

        val requestObject = baseAgentRequestObject.note(generateRandomString(1999)).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(storyId = 1554)
    void shouldCreateAgentWhenNoteIsEmpty() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.note("").token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

    }

    @Test
    @UserStoryId(storyId = 1554)
    void documentTypeIsRequired() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val imagesObj = ImageModel
                .builder()
                .name(FakerUtils.title)
                .content(content)
                .build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{imagesObj}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, containsString("Cannot convert null value"));
    }

    @Test
    @UserStoryId(storyId = 1554)
    void imageIsRequired() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(IMAGE_IS_REQUIRED_MSG));
    }

    @Test
    @UserStoryId(storyId = 1554)
    void imageNameIsRequired() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val imagesObj = ImageModel
                .builder()
                .documentType(DocumentType.PASSPORT.getValue())
                .content(content)
                .build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{imagesObj}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(NAME_REQUIRED_MSG));
    }

    @Test
    @UserStoryId(storyId = 1554)
    void shouldNotCreateAgentWhenImageNameLengthIsMoreThen100() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val imagesObj = ImageModel
                .builder()
                .documentType(DocumentType.PASSPORT.getValue())
                .name(generateRandomString(101))
                .content(content)
                .build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{imagesObj}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(NAME_VALIDATION_MSG));
    }

    @Test
    @UserStoryId(storyId = 1554)
    void contentIsRequired() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val imagesObj = ImageModel
                .builder()
                .documentType(DocumentType.PASSPORT.getValue())
                .name(generateRandomString(99))
                .build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{imagesObj}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(CONTENT_IS_REQUIRED_MSG));
    }

    @Test
    @UserStoryId(storyId = 1554)
    void contentShouldBeBase64() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val imagesObj = ImageModel
                .builder()
                .documentType(DocumentType.PASSPORT.getValue())
                .name(generateRandomString(99))
                .content(generateRandomString())
                .build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{imagesObj}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(CONTENT_IS_NOT_BASE64_MSG));
    }


    @Test
    @UserStoryId(storyId = 1554)
    void contentShouldBeLessThen10MB() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val imagesObj = ImageModel
                .builder()
                .documentType(DocumentType.PASSPORT.getValue())
                .name(generateRandomString(99))
                .content(bigImage)
                .build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.images(new ImageModel[]{imagesObj}).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(IMAGE_SIZE_VALIDATION_MSG));
    }

    @Test
    @UserStoryId(storyId = 1779)
    void ibanShouldBeValid() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val bankInfoObj = baseBankInfoObj.iban(generateRandomString()).build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.bankInfo(bankInfoObj).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(IBAN_INVALID_MSG));
    }

    @Test
    @UserStoryId(storyId = 1779)
    void swiftShouldBeValid() {
        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        waitForCustomerBalanceUpdate(customerId);

        val bankInfoObj = baseBankInfoObj.swift(generateRandomString()).build();

        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val requestObject = baseAgentRequestObject.bankInfo(bankInfoObj).token(customerToken).build();

        confirmRegistration(user.getEmail(), user.getPassword());

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(MODEL_VALIDATION_FAILURE))
                .body(MESSAGE_FIELD, equalTo(SWIFT_INVALID_MSG));
    }

    @Test
    @UserStoryId(storyId = 1779)
    void ibanAndSwiftAreNotRequired() {
        val customerEmail = generateRandomEmail();
        val customerPass = generateValidPassword();
        val customerId = registerCustomer(new RegistrationRequestModel());

        waitForCustomerBalanceUpdate(customerId);

        val bankInfoObj = BankInfoModel
                .builder()
                .beneficiaryName(FakerUtils.fullName)
                .bankName(FakerUtils.companyName)
                .bankBranch(FakerUtils.city)
                .accountNumber(FakerUtils.phoneNumber)
                .bankAddress(FakerUtils.address)
                .bankBranchCountryId(1)
                .build();

        val customerToken = getUserToken(customerEmail, customerPass);
        val requestObject = baseAgentRequestObject.bankInfo(bankInfoObj).token(customerToken).build();

        confirmRegistration(customerEmail, customerPass);

        AgentsUtils.createAgent(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    private void waitForCustomerBalanceUpdate(String customerId) {
        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Customer's balance: " + Double.valueOf(getCustomerBalance(customerId)));
                    log.info("===============================================================================");
                    return REQUIRED_NUMBER_OF_TOKENS <= Double.valueOf(getCustomerBalance(customerId));
                });
    }
}
