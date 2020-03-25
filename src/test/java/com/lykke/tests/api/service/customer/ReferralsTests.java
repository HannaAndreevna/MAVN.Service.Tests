package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.common.GenerateUtils.generateName;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.agentmanagement.AgentManagementUtils.registerDefaultAgent;
import static com.lykke.tests.api.service.customer.CustomerReferralsUtils.getAllReferrals;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getCustomerToken;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.ReferralsUtils.getReferralLeadsForACustomer;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.approveLead;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.createReferralLead;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.getReferralLeadsResponse;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.CommonConsts.RegistrationData;
import com.lykke.tests.api.service.customer.model.ReferralsLeadRequest;
import com.lykke.tests.api.service.customer.model.referral.CommonReferralStatus;
import com.lykke.tests.api.service.customer.model.referral.ReferralPaginationRequestModel;
import com.lykke.tests.api.service.customer.model.referral.ReferralsListResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadsManualEntryModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsCreateModel;
import java.time.Instant;
import java.util.Random;
import java.util.stream.Stream;
import lombok.val;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReferralsTests extends BaseApiTest {

    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";
    private static final String INVALID_EMAIL_FORMAT_ERROR = "InvalidEmailFormat";
    private static final String MODEL_VALIDATION_FAILED_ERROR = "ModelValidationFailed";
    private static final String EMAIL_FIELD_IS_REQUIRED_MESSAGE = "The Email field is required.";
    private static final String NUMBER_FIELD_IS_REQUIRED_MESSAGE = "The Number field is required.";
    private static final String NAME_FIELD_IS_REQUIRED_MESSAGE = "The Name field is required.";
    private static final String SALESFORCE_ID = "salesforceId";
    private static final String PENDING_STATUS = "Pending";
    private static final String APPROVED_STATUS = "Approved";
    private static final String DISABLED_MESAGE = "The test fails because of the latest changes introduced in FAL-1304."
            + " The customer should be converted to agent and his email should be verified and confirmed which "
            + "cannot be achieved by the automation right now.";
    private static final String SHORT_NAME = generateName(2);
    private static final String TEST_NAME = generateName(6);
    private static final String LONG_NAME = generateName(120);
    private static final String VALID_PHONE_NUMBER = "110-408-6552";
    private static final String INVALID_PHONE_NUMBER = "110.408.6552";

    static Stream<Arguments> invalidValues() {
        return Stream.of(
                of(EMPTY, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(SHORT_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(LONG_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, EMPTY, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, SHORT_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, LONG_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, EMPTY, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, TEST_NAME, EMPTY, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, EMPTY, generateRandomEmail(), FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, INVALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, EMPTY, FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, TEST_NAME, FakerUtils.randomQuote),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(), EMPTY)
        );
    }

    static Stream<Arguments> requiredFields() {
        return Stream.of(
                // TODO: wrong input for the test
                /*
                of(null, FakerUtils.phoneNumber, generateRandomEmail(), generateRandomString(),
                        MODEL_VALIDATION_FAILED_ERROR, NAME_FIELD_IS_REQUIRED_MESSAGE),
                of(FakerUtils.fullName, null, generateRandomEmail(), generateRandomString(),
                        MODEL_VALIDATION_FAILED_ERROR, NUMBER_FIELD_IS_REQUIRED_MESSAGE),
                of(FakerUtils.fullName, FakerUtils.phoneNumber, null, generateRandomString(),
                        INVALID_EMAIL_FORMAT_ERROR, EMAIL_FIELD_IS_REQUIRED_MESSAGE)
                */
                of(FakerUtils.firstName, FakerUtils.lastName, String.valueOf(FakerUtils.countryPhoneCode),
                        FakerUtils.country, String.valueOf(new Random(100).nextInt()), generateRandomEmail(),
                        generateRandomString(100), MODEL_VALIDATION_FAILED_ERROR, NAME_FIELD_IS_REQUIRED_MESSAGE)
        );
    }

    // TODO: fix test
    @UserStoryId(storyId = {1148, 1414, 1557})
    @ParameterizedTest(name = "Run {index}: firstName={0}, lastName={1}, countryCode={2}, countryName={3}, number={4}, email={5}, note={6}")
    @MethodSource("invalidValues")
    void shouldCheckForValidPropertyReferralValues(String firstName, String lastName, String countryCode,
            String countryName, String number, String email, String note) {
        val customerToken = getCustomerToken();
        val requestObject = ReferralsLeadRequest
                .builder()
                .firstName(firstName)
                .lastName(lastName)
                .countryCode(countryCode)
                .countryName(countryName)
                .number(number)
                .email(email)
                .note(note)
                .build();

        val response = ReferralsUtils.addPropertyReferralForACustomer(customerToken, requestObject);

        assertEquals(requestObject.getValidationResponse(), response);
    }

    // TODO: check test
    @UserStoryId(storyId = 1414)
    @ParameterizedTest(name = "Run {index}: firstName={0}, lastName={1}, countryCode={2}, countryName={3}, number={4}, email={5}, note={6}, error={7}, message={8}")
    @MethodSource("requiredFields")
    void emailFieldIsRequired(String firstName, String lastName, String countryCode,
            String countryName, String number, String email, String note, String error, String message) {
        val customerToken = getCustomerToken();

        ReferralsUtils
                .addLeadReferralForCustomer(customerToken, firstName, lastName, countryCode, countryName, number, email,
                        note)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(ERROR_FIELD, equalTo(error))
                .body(MESSAGE_FIELD, equalTo(message));
    }

    ////55  @Disabled("TODO: fix assertions")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1388, 3759, 3971})
    void shouldGetListOfLeadsForACustomer() {
        val customerData01 = registerDefaultAgent();
        val customerData02 = registerDefaultAgent();
        val referralLeadsRequestObj_1 = createReferralLeadsRequestObject(customerData01.getCustomerId(),
                customerData01.getCampaignId());
        val referralLeadsRequestObj_2 = createReferralLeadsRequestObject(customerData02.getCustomerId(),
                customerData02.getCampaignId());

        createReferralLead(referralLeadsRequestObj_1);
        createReferralLead(referralLeadsRequestObj_2);

        // TODO: no referral leads
        val referralLeadId_1 = getReferralLeadsResponse(customerData01.getCustomerId()).getReferralLeads()[
                getReferralLeadsResponse(customerData01.getCustomerId()).getReferralLeads().length - 1].getId();

        approveLead(referralLeadId_1);

        val customerToken = getUserToken(customerData01);
        val referralLeadResponse = getReferralLeadsForACustomer(customerToken);

        val actualResult = getAllReferrals(ReferralPaginationRequestModel
                .referralPaginationRequestModelBuilder()
                .status(CommonReferralStatus.ONGOING)
                .currentPage(1)
                .pageSize(500)
                .build(), getUserToken(customerData01))
                .then()

                ////55
                .log().all()

                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReferralsListResponseModel.class);

        assertAll(
                () -> assertEquals(
                        referralLeadsRequestObj_1.getFirstName() + " " + referralLeadsRequestObj_1.getLastName(),
                        referralLeadResponse.getLeadReferrals()[referralLeadResponse.getLeadReferrals().length - 1]
                                .getName()),
                () -> assertEquals(APPROVED_STATUS,
                        referralLeadResponse.getLeadReferrals()[referralLeadResponse.getLeadReferrals().length - 1]
                                .getStatus()),
                () -> assertTrue(
                        referralLeadResponse.getLeadReferrals()[referralLeadResponse.getLeadReferrals().length - 1]
                                .getTimeStamp().contains(Instant.now().toString().split("T")[0])),
                () -> assertEquals(
                        referralLeadsRequestObj_2.getFirstName() + " " + referralLeadsRequestObj_2.getLastName(),
                        referralLeadResponse.getLeadReferrals()[referralLeadResponse.getLeadReferrals().length - 2]
                                .getName()),
                () -> assertEquals(PENDING_STATUS,
                        referralLeadResponse.getLeadReferrals()[referralLeadResponse.getLeadReferrals().length - 2]
                                .getStatus()),
                () -> assertTrue(
                        referralLeadResponse.getLeadReferrals()[referralLeadResponse.getLeadReferrals().length - 2]
                                .getTimeStamp().contains(Instant.now().toString().split("T")[0]))
        );
    }


    private ReferralLeadsCreateModel createReferralLeadsRequestObject(String customerId, String campaignId) {
        return ReferralLeadsCreateModel
                .builder()
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .phoneCountryCodeId(RegistrationData.COUNTRY_OF_NATIONALITY_ID_01)
                .email(generateRandomEmail())
                .note(FakerUtils.randomQuote)
                .customerId(customerId)
                .campaignId(campaignId)
                .build();
    }

    private ApprovedLeadsManualEntryModel createApprovedLeadsManualEntryRequestObject(String referralLeadId) {
        return ApprovedLeadsManualEntryModel
                .builder()
                .leads(new ApprovedLeadManualEntryModel[]{
                        ApprovedLeadManualEntryModel
                                .builder()
                                .mvnReferralId(referralLeadId)
                                .salesforceId(SALESFORCE_ID)
                                .build()
                })
                .build();
    }
}
