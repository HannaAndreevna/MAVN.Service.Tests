package com.lykke.tests.api.service.referral;

import static com.lykke.tests.api.common.CommonConsts.UUID_REGEX;
import static com.lykke.api.testing.api.common.GenerateUtils.generateName;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryPropertyPurchasesByLeads;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.approveLead;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.createReferralLead;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.getReferralLeadsApprovedResponse;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.getReferralLeadsPropertyPurchaseResponse;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.getReferralLeadsResponse;
import static com.lykke.tests.api.service.referral.ReferralLeadsUtils.getReferralLeadsStatistic;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadsManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PropertyPurchaseByLeadManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PropertyPurchasesByLeadsManualEntryModel;
import com.lykke.tests.api.service.referral.model.referralleadmodel.ReferralLeadsCreateRequest;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReferralLeadsTests extends BaseApiTest {

    private static final String NO_ERR_MSG = "None";
    private static final String SALESFORCE_ID = "salesforceId";
    private static final String PENDING_STATE = "Pending";
    private static final String DISABLED_MESAGE = "The test fails because of the latest changes introduced in FAL-1304."
            + " The customer should be converted to agent and his email should be verified and confirmed which "
            + "cannot be achieved by the automation right now.";
    private static final String SHORT_NAME = generateName(2);
    private static final String TEST_NAME = generateName(6);
    private static final String LONG_NAME = generateName(120);
    private static final String VALID_PHONE_NUMBER = "110-408-6552";
    private static final String INVALID_PHONE_NUMBER = "110.408.6552";

    static Stream<Arguments> invalidValues() {
        val customerId = registerCustomer();
        return Stream.of(
                of(EMPTY, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote,
                        UUID.randomUUID().toString()),
                of(SHORT_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(LONG_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(generateRandomString().concat("%#$"), TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER,
                        generateRandomEmail(), FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, EMPTY, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, SHORT_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, LONG_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, generateRandomString().concat("%#$"), TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER,
                        generateRandomEmail(), FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, EMPTY, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, EMPTY, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, EMPTY, generateRandomEmail(), FakerUtils.randomQuote,
                        UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, INVALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, EMPTY, FakerUtils.randomQuote,
                        UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, TEST_NAME, FakerUtils.randomQuote,
                        UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(), EMPTY,
                        UUID.randomUUID().toString()),
                of(TEST_NAME, TEST_NAME, TEST_NAME, TEST_NAME, VALID_PHONE_NUMBER, generateRandomEmail(),
                        FakerUtils.randomQuote, EMPTY)
        );
    }

    @Disabled("TODO: error 500")
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1811)
    public void shouldGetReferralLeadsStatistic() {
        val referralLeadsStatisticsResponse = getReferralLeadsStatistic();

        //TODO(This needs to be finished when can create approved agents.)
//        val customerId = registerCustomer();
//        val referralLeadsRequestObject = createReferralLeadsRequestObject(customerId);
//
//        createReferralLead(referralLeadsRequestObject);
//
//        val referralLeadsStatisticsResponseUpdated = getReferralLeadsStatistic();
//
//        assertAll(
//                () -> assertNotEquals(referralLeadsStatisticsResponse.getNumberOfLeads(),
//                        referralLeadsStatisticsResponseUpdated.getNumberOfLeads()));

    }

    @Disabled(DISABLED_MESAGE)
    @Test
    @UserStoryId(storyId = {1150, 1391})
    public void shouldCreateAndGetReferralLeads() {
        val customerId = getRandomUuid();
        val referralLeadsRequestObject = createReferralLeadsRequestObject(customerId);

        createReferralLead(referralLeadsRequestObject);

        val referralLeadsResponse = getReferralLeadsResponse(customerId);

        assertAll(
                () -> assertEquals(NO_ERR_MSG, referralLeadsResponse.getErrorCode()),
                () -> assertEquals(null, referralLeadsResponse.getErrorMessage()),
                () -> assertEquals(referralLeadsRequestObject.getFirstName(),
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getFirstName()),
                () -> assertEquals(referralLeadsRequestObject.getLastName(),
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getLastName()),
                () -> assertEquals(referralLeadsRequestObject.getEmail(),
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getEmail()),
                () -> assertEquals(referralLeadsRequestObject.getNote(),
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getNote()),
                () -> assertEquals(referralLeadsRequestObject.getPhoneNumber(),
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getPhoneNumber()),
                () -> assertEquals(customerId,
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getAgentId()),
                () -> assertEquals(PENDING_STATE,
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getState()),
                () -> assertEquals(null,
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getSalesforceId()),
                () -> assertTrue(
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getId().matches(UUID_REGEX)),
                () -> assertTrue(
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getAgentSalesforceId().matches(UUID_REGEX)),
                () -> assertTrue(
                        referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length - 1]
                                .getCreationDateTime().contains(Instant.now().toString().split("T")[0]))
        );
    }

    @Disabled(DISABLED_MESAGE)
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1150, 1391})
    public void shouldGetReferralLeads_Approved() {
        val customerId = getRandomUuid();
        val referralLeadsRequestObject = createReferralLeadsRequestObject(customerId);
        createReferralLead(referralLeadsRequestObject);
        val referralLeadsResponse = getReferralLeadsResponse(customerId);
        val referralLeadId = referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length
                - 1].getId();

        approveLead(referralLeadId);

        val referralLeadsApprovedResponse = getReferralLeadsApprovedResponse();

        assertAll(
                () -> assertEquals(NO_ERR_MSG, referralLeadsApprovedResponse.getErrorCode()),
                () -> assertEquals(null, referralLeadsApprovedResponse.getErrorMessage()),
                () -> assertTrue(referralLeadsApprovedResponse.getReferralLeads()[
                        referralLeadsApprovedResponse.getReferralLeads().length - 1].getId().matches(UUID_REGEX)),
                () -> assertTrue(referralLeadsApprovedResponse.getReferralLeads()[
                        referralLeadsApprovedResponse.getReferralLeads().length - 1].getReferralLeadId()
                        .matches(UUID_REGEX)),
                () -> assertTrue(referralLeadsApprovedResponse.getReferralLeads()[
                        referralLeadsApprovedResponse.getReferralLeads().length - 1].getTimestamp()
                        .contains(Instant.now().toString().split("T")[0]))
        );
    }

    @Disabled(DISABLED_MESAGE)
    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = 1150)
    public void shouldGetReferralLeads_PropertyPurchase() {
        val customerId = getRandomUuid();
        createReferralLead(createReferralLeadsRequestObject(customerId));
        val referralLeadsResponse = getReferralLeadsResponse(customerId);
        val referralLeadId = referralLeadsResponse.getReferralLeads()[referralLeadsResponse.getReferralLeads().length
                - 1].getId();

        PropertyPurchasesByLeadsManualEntryModel requestObject = createPropertyPurchasesByLeadsManualEntryRequestObject(
                referralLeadId);

        postManualEntryPropertyPurchasesByLeads(requestObject);

        val referralLeadsPropertyPurchaseResponse = getReferralLeadsPropertyPurchaseResponse();

        assertAll(
                () -> assertEquals(referralLeadId, referralLeadsPropertyPurchaseResponse.getPropertyPurchases()[
                        referralLeadsPropertyPurchaseResponse.getPropertyPurchases().length - 1].getReferralLeadId()));
    }

    @UserStoryId(storyId = 1391)
    @ParameterizedTest(name = "Run {index}: firstName={0}, lastName={1}, countryCode={2}, countryName={3}, number={4}, email={5}, note={6}, customerId={7}")
    @MethodSource("invalidValues")
    public void shouldValidateReferralLeadsFields(String firstName, String lastName, String countryCode,
            String countryName, String phoneNumber, String email,
            String note, String customerId) {
        val requestObject = ReferralLeadsCreateRequest
                .builder()
                .firstName(firstName)
                .lastName(lastName)
                .countryCode(countryCode)
                .countryName(countryName)
                .phoneNumber(phoneNumber)
                .email(email)
                .note(note)
                .customerId(customerId)
                .build();

        val response = createReferralLead(requestObject);

        assertEquals(requestObject.getValidationResponse(), response);
    }


    private PropertyPurchasesByLeadsManualEntryModel createPropertyPurchasesByLeadsManualEntryRequestObject(
            String referralLeadId) {
        return PropertyPurchasesByLeadsManualEntryModel
                .builder()
                .propertyPurchasesByLeads(new PropertyPurchaseByLeadManualEntryModel[]{
                        PropertyPurchaseByLeadManualEntryModel
                                .builder()
                                .mvnReferralId(referralLeadId)
                                .timestamp(Instant.now().toString())
                                .build()
                })
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

    private ReferralLeadsCreateRequest createReferralLeadsRequestObject(String customerId) {
        return ReferralLeadsCreateRequest
                .builder()
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .phoneNumber(FakerUtils.phoneNumber)
                .phoneCountryCodeId(1)
                .email(generateRandomEmail())
                .note(FakerUtils.randomQuote)
                .customerId(customerId)
                .build();
    }
}
