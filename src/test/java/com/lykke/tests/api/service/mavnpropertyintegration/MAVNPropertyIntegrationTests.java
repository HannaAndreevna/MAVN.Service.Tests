package com.lykke.tests.api.service.mavnpropertyintegration;

import static com.lykke.tests.api.base.BasicFunctionalities.getTomorrowDateString;
import static com.lykke.tests.api.base.BasicFunctionalities.getYesterdayDateString;
import static com.lykke.tests.api.common.CommonConsts.DEFAULT_CURRENCY;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.getPendingInvoicePayments;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postHistoryAgentsChangedSalesman;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postHistoryApprovedLeads;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postHistoryPaidInvoices;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postHistoryPropertyPurchasesByLeads;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postHistoryRegisteredLeads;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postIntegrationPaidInvoices;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postLead;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryAgentsChangedSalesmen;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryApprovedLeads;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryLeadsChangedSalesmen;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryPaidInvoices;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryPropertyPurchasesByLeads;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryRegisteredLeads;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentChangedSalesmanManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentsChangedSalesmenManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ApprovedLeadsManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetAgentsChangedSalesmenHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetApprovedLeadsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetPropertyPurchasesByLeadsRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetRegisteredLeadsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.HistoryPaidInvoiceRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.HistoryPaidInvoiceResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.IntegrationPaidInvoicesRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.InvoiceDetailsModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadChangedSalesmanManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadRegisterRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadRegisterResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadStatus;
import com.lykke.tests.api.service.mavnpropertyintegration.model.LeadsChangedSalesmenManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ManualPaidInvoicesRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaginatedResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PaidInvoicesResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PendingInvoicePaymentsResponseModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PropertyPurchaseByLeadManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.PropertyPurchasesByLeadsManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.RegisterLeadManualEntryModel;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MAVNPropertyIntegrationTests extends BaseApiTest {

    private static final String STATUS_FIELD = "status";
    private static final String SALESFORCE_ID_FIELD = "SalesforceId";
    private static final String SALESMAN_SALESFORCE_ID_FIELD = "SalesmanSalesforceId";
    private static final String ERROR_CODE_FIELD = "errorCode";
    private static final String REFER_ID_FIELD = "ReferId";

    private static final String VALID_REFER_ID = "id";
    private static final String VALID_LEAD_NAME = "name";
    private static final String VALID_LEAD_PHONE_NUMBER = "3355234";
    private static final String VALID_LEAD_PHONE_CODE = "+888";
    private static final String VALID_LEAD_NOTE = "note";
    private static final String RESPONSE_STATUS_SUCCESS = "success";
    private static final String RESPONSE_STATUS_ERROR = "error";

    private static final int CURRENT_PAGE = 1;
    private static final int PAGE_SIZE = 11;

    private static final String INVALID_REFER_ID = EMPTY;
    private static final String INVALID_LEAD_NAME = EMPTY;
    private static final String INVALID_LEAD_PHONE_NUMBER = EMPTY;

    private static final String STATUS = "status";
    private static final String SALESFORCE_ID = "salesforceId";
    private static final String ERROR_CODE = "errorCode";
    private static final String PHONE_COUNTRY_CODE_FIELD = "PhoneCountryCode[0]";
    private static final String PHONE_COUNTRY_NAME_FIELD = "PhoneCountryName[0]";
    private static final String PHONE_COUNTRY_CODE_REQUIRED_MSG = "The PhoneCountryCode field is required.";
    private static final String PHONE_COUNTRY_CODE_MAX_LENGTH_MSG = "The field PhoneCountryCode must be a string or array type with a maximum length of '20'.";
    private static final String PHONE_COUNTRY_NAME_REQUIRED_MSG = "The PhoneCountryName field is required.";
    private static final String PHONE_COUNTRY_NAME_MAX_LENGTH_MSG = "The field PhoneCountryName must be a string or array type with a maximum length of '90'.";
    private static final LeadRegisterResponseModel expectedResponse =
            LeadRegisterResponseModel
                    .builder()
                    .status(STATUS)
                    .salesforceId(SALESFORCE_ID)
                    .errorCode(ERROR_CODE)
                    .build();

    private static final String TECHNICAL_PROBLEM_MESSAGE = "Technical problem";
    private static final String DISABLED_MESSAGE =
            "the functionality will be available for testing after MVN provides us with test environment";

    private static final String PHONE_COUNTRY_CODE_0_FIELD = "PhoneCountryCode[0]";
    private static final String PHONE_COUNTRY_NAME_0_FIELD = "PhoneCountryName[0]";
    private static final String PHONE_COUNTRY_CODE_IS_REQUIRED_MESSAGE = "The PhoneCountryCode field is required.";
    private static final String PHONE_COUNTRY_NAME_IS_REQUIRED_MESSAGE = "The PhoneCountryName field is required.";
    private static final String PHONE_COUNTRY_CODE_LENGTH_VALIDATION_MESSAGE = "The field PhoneCountryCode must be "
            + "a string or array type with a maximum length of '20'.";
    private static final String PHONE_COUNTRY_NAME_LENGTH_VALIDATION_MESSAGE = "The field PhoneCountryName must be "
            + "a string or array type with a maximum length of '90'.";

    private static final int INVALID_LENGTH = 500;
    private static final int SOME_VAT_AMOUNT = 200;
    private static final int SOME_DISCOUNT_AMOUNT = 100;
    private static final int SOME_NET_PROPERTY_PRICE = 1800;
    private static final int SOME_SELLING_PROPERTY_PRICE = 2000;

    private static final String LOCATION_CODE = "EB Beach Vista T2-20-2007";
    private static final String PAYMENT_TYPE = "Installment";
    private static final String SOURCE = "Oracle";
    private static final String EXPECTED_STATUS = "Overdue";
    private static final String CUSTOMER_TRX_ID = "50846643";
    private static final String TRX_DATE = "2019-04-15T00:00:00";
    private static final String ORG_ID = "81";
    private static final String INVOICE_CURRENCY_CODE = "AED";
    private static final String DOC_SEQUENCE_VALUE = "1852070";
    private static final String INVOICE_DESCRIPTION = "EB Beach Vista T2-20-2007";
    private static final int LINE_AMOUNT = 81694;
    private static final int TAX_AMOUNT = 0;
    private static final int LINE_TOTAL_AMOUNT = 81694;
    private static final int INVOICE_AMOUNT = 81694;
    private static final int INVOICE_AMOUNT_REMAIN = 81693;
    private static final String GL_DATE = "2019-04-15T00:00:00";
    private static final String DUE_DATE = "2019-04-15T00:00:00";
    private static final String TYPE = "INV";
    private static final String CUSTOMER_TRX_TYPE_NAME = "EB BCHVT T2 Sale";
    private static final String PARTY_ID = "2417";
    private static final String CUSTOMER_ACCOUNT_ID = "14961247";
    private static final String ACCOUNT_NUMBER = "394954";
    private static final String EXPECTED_LOCATION_CODE = "EB Beach Vista T2-20-2007";
    private static final String INV_FLAG = "Y";
    private static final String IS_LPF = "N";
    private static final String SERVICE_FLAG = "N";
    private static final String ORACLE_EMAIL = "ZRD6T1AL0OD5TJO@email.com";

    private String customerId;

    static Stream<Arguments> getInvalidSourceData() {
        /*
        /api/leads

        "ReferId": required,
        "LeadName": required,
        "LeadPhoneNumber": required,
        "LeadNote": optional
        */
        return Stream.of(
                of(INVALID_REFER_ID, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE),
                of(VALID_REFER_ID, INVALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE),
                of(VALID_REFER_ID, VALID_LEAD_NAME, INVALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE),
                of(generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH))
        );
    }

    static Stream<Arguments> getInvalidManualEntryRegisteredLeadsParameters() {
        /*
        /api/manualEntry/registeredLeads

        "ReferId": required,
        "LeadName": required"
        LeadPhoneNumber": required
        "LeadNote": optional,
        "ResponseStatus": required
        "ResponseSalesforceId": required only if ResponseStatus is “success”
        "ResponseErrorCode": "required only if ResponseStatus is not “success”
        */
        val referId = registerCustomer();
        return Stream.of(
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE, SALESFORCE_ID,
                        RESPONSE_STATUS_ERROR),
                of(EMPTY, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE, SALESFORCE_ID,
                        RESPONSE_STATUS_SUCCESS),
                of(referId, EMPTY, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE, SALESFORCE_ID, RESPONSE_STATUS_ERROR),
                of(referId, VALID_LEAD_NAME, EMPTY, VALID_LEAD_NOTE, SALESFORCE_ID, RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE, EMPTY, RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, VALID_LEAD_NOTE, SALESFORCE_ID, EMPTY),
                of(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                of(generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH))
        );
    }

    static Stream<Arguments> getInvalidManualEntryApprovedLeadsParameters() {
        /*
        /api/manualEntry/approvedLeads

        {"FromTimestamp": required,
        "ToTimestamp": required,
        "Leads": [  {
            "Timestamp": required,
            "ReferId": required,
            "SalesforceId": required  }]}
        */
        val referId = registerCustomer();
        return Stream.of(
                of(EMPTY, SALESFORCE_ID, Instant.now().minus(1, ChronoUnit.MINUTES).toString(),
                        Instant.now().toString(), Instant.now().toString()),
                of(referId, EMPTY, Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        Instant.now().toString()),
                of(referId, SALESFORCE_ID, EMPTY, Instant.now().toString(), Instant.now().toString()),
                of(referId, SALESFORCE_ID, Instant.now().minus(1, ChronoUnit.MINUTES).toString(), EMPTY,
                        Instant.now().toString()),
                of(referId, SALESFORCE_ID, Instant.now().minus(1, ChronoUnit.MINUTES).toString(),
                        Instant.now().toString(), EMPTY),
                of(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                of(generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH))
        );
    }

    static Stream<Arguments> getInvalidPhoneCountryCodeAndPhoneCountryName() {
        return Stream.of(
                of(EMPTY, FakerUtils.country, PHONE_COUNTRY_CODE_0_FIELD, PHONE_COUNTRY_CODE_IS_REQUIRED_MESSAGE),
                of(null, FakerUtils.country, PHONE_COUNTRY_CODE_0_FIELD, PHONE_COUNTRY_CODE_IS_REQUIRED_MESSAGE),
                of(generateRandomString(21), FakerUtils.country, PHONE_COUNTRY_CODE_0_FIELD,
                        PHONE_COUNTRY_CODE_LENGTH_VALIDATION_MESSAGE),
                of(VALID_LEAD_PHONE_CODE, EMPTY, PHONE_COUNTRY_NAME_0_FIELD, PHONE_COUNTRY_NAME_IS_REQUIRED_MESSAGE),
                of(VALID_LEAD_PHONE_CODE, null, PHONE_COUNTRY_NAME_0_FIELD, PHONE_COUNTRY_NAME_IS_REQUIRED_MESSAGE),
                of(VALID_LEAD_PHONE_CODE, generateRandomString(91), PHONE_COUNTRY_NAME_0_FIELD,
                        PHONE_COUNTRY_NAME_LENGTH_VALIDATION_MESSAGE)
        );
    }

    static Stream<Arguments> getInvalidManualEntryPropertyPurchasesByLeadsParameters() {
        /*
        /api/manualEntry/propertyPurchasesByLeads

        {"FromTimestamp": required,
        "ToTimestamp": required,
        "PropertyPurchasesByLeads": [  {
            "Timestamp": required,
            "ReferId": required,
            "SalesforceId": required  }]}
        */
        return getInvalidManualEntryApprovedLeadsParameters();
    }

    static Stream<Arguments> getInvalidHistoryRegisteredLeadsParameters() {
        /*
        fromTimestamp (UTC, optional)
        toTimestamp (UTC, optional)
        referId (optional)
        responseSalesforceId (optional)
        leadName (optional)
        leadPhoneNumber (optional)
        responseStatus (optional)
        */

        val referId = registerCustomer();
        return Stream.of(
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        RESPONSE_STATUS_ERROR),
                of(EMPTY, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        RESPONSE_STATUS_SUCCESS),
                of(referId, EMPTY, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, EMPTY, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, EMPTY,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(),
                        RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID, EMPTY, Instant.now().toString(),
                        RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), EMPTY, RESPONSE_STATUS_SUCCESS),
                of(referId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID,
                        Instant.now().minus(1, ChronoUnit.MINUTES).toString(), Instant.now().toString(), EMPTY),
                of(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                of(generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH), generateRandomString(INVALID_LENGTH),
                        generateRandomString(INVALID_LENGTH))
        );
    }

    static Stream<Arguments> getInvalid_CountryCode_CountryName() {
        return Stream.of(
                of(EMPTY, PHONE_COUNTRY_CODE_FIELD, PHONE_COUNTRY_CODE_REQUIRED_MSG),
                of(generateRandomString(21), PHONE_COUNTRY_CODE_FIELD, PHONE_COUNTRY_CODE_MAX_LENGTH_MSG),
                of(EMPTY, PHONE_COUNTRY_NAME_FIELD, PHONE_COUNTRY_NAME_REQUIRED_MSG),
                of(generateRandomString(91), PHONE_COUNTRY_NAME_FIELD, PHONE_COUNTRY_NAME_MAX_LENGTH_MSG)
        );
    }

    @BeforeEach
    void setUpUser() {
        customerId = registerCustomer();
    }

    // TODO: accordingly to my observation, this test may work from the second run
    @Test
    @UserStoryId(2578)
    void shouldGetPendingInvoicePaymentByEmail() {

        val expectedInvoiceDetails = InvoiceDetailsModel.builder()
                .customerTrxId(CUSTOMER_TRX_ID)
                .trxDate(TRX_DATE)
                .orgId(ORG_ID)
                .invoiceCurrencyCode(INVOICE_CURRENCY_CODE)
                .docSequenceValue(DOC_SEQUENCE_VALUE)
                .invoiceDescription(INVOICE_DESCRIPTION)
                .lineAmount(LINE_AMOUNT)
                .taxAmount(TAX_AMOUNT)
                .lineTotalAmount(LINE_TOTAL_AMOUNT)
                .invoiceAmount(INVOICE_AMOUNT)
                .invoiceAmountRemain(INVOICE_AMOUNT_REMAIN)
                .glDate(GL_DATE)
                .dueDate(DUE_DATE)
                .type(TYPE)
                .customerTrxTypeName(CUSTOMER_TRX_TYPE_NAME)
                .partyId(PARTY_ID)
                .customerAccountId(CUSTOMER_ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .locationCode(EXPECTED_LOCATION_CODE)
                .invFlag(INV_FLAG)
                .isLpf(IS_LPF)
                .serviceFlag(SERVICE_FLAG)
                .build();

        InvoiceDetailsModel[] invoice = new InvoiceDetailsModel[1];

        invoice[0] = expectedInvoiceDetails;

        val expectedPendingInvoices = PendingInvoicePaymentsResponseModel.builder()
                .locationCode(LOCATION_CODE)
                .paymentType(PAYMENT_TYPE)
                .source(SOURCE)
                .status(EXPECTED_STATUS)
                .invoiceDetail(invoice)
                .build();

        val pendingInvoices = getPendingInvoicePayments(ORACLE_EMAIL);

        val actualPendingInvoice = pendingInvoices[2].getInvoiceDetail();
        val actualInvoiceDetails = actualPendingInvoice[0];

        assertAll(
                () -> assertEquals(expectedPendingInvoices.getInvoiceDetail()[0], actualInvoiceDetails)
        );
    }

    @ParameterizedTest(name = "Run {index}: value={0}, field={1}, errMsg={2}")
    @MethodSource("getInvalid_CountryCode_CountryName")
    @UserStoryId(storyId = 1738)
    void shouldNotRegisterLead_Invalid_CountryCode_CountryName(String value, String field, String errMsg) {
        val requestObject = LeadRegisterRequestModel
                .builder()
                .phoneCountryCode(value)
                .phoneCountryName(value)
                .build();
        postLead(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(errMsg));

    }


    @Disabled(DISABLED_MESSAGE)
    @Test
    @UserStoryId(storyId = {1119, 1222})
    void shouldRespondOnValidDataSent() {
        val requestObject = LeadRegisterRequestModel
                .builder()
                .referId(customerId)
                .build();

        val actualResponse = postLead(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                // TODO: return data are now hypothetical
                .body(STATUS_FIELD, equalTo(expectedResponse.getStatus()))
                .body(SALESFORCE_ID_FIELD, equalTo(expectedResponse.getSalesforceId()))
                .body(ERROR_CODE_FIELD, equalTo(expectedResponse.getErrorCode()))
                .extract()
                .as(LeadRegisterResponseModel.class);

        assertAll(
                () -> assertEquals(expectedResponse.getStatus(), actualResponse.getStatus()),
                () -> assertEquals(expectedResponse.getSalesforceId(), actualResponse.getSalesforceId()),
                () -> assertEquals(expectedResponse.getErrorCode(), actualResponse.getErrorCode())
        );
        /*
        "success" == actualResponse.getStatus()
                ? assertNotEquals(EMPTY, actualResponse.getSalesforceId())
                : assertNotEquals(EMPTY, actualResponse.getErrorCode());
        */
    }

    @ParameterizedTest
    @MethodSource("getInvalidSourceData")
    @UserStoryId(storyId = {1119, 1222, 1266})
    void shouldRespondWithErrorOnInvalidDataSent(
            String referId, String leadName, String leadPhoneNumber, String leadNote) {
        val requestObject = LeadRegisterRequestModel
                .builder()
                .referId(referId)
                .build();

        postLead(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(storyId = {1209, 1257})
    void shouldReturnManuallyRegisteredLeads() {
        val requestObject = RegisterLeadManualEntryModel
                .builder()
                .referId(customerId)
                .email(generateRandomEmail())
                .firstName(VALID_LEAD_NAME)
                .lastName(VALID_LEAD_NAME)
                .phoneNumber(VALID_LEAD_PHONE_NUMBER)
                .phoneCountryCode(VALID_LEAD_PHONE_CODE)
                .phoneCountryName(FakerUtils.country)
                .agentSalesforceId(generateRandomString(10))
                .leadNote(VALID_LEAD_NOTE)
                .responseSalesforceId(SALESFORCE_ID)
                .responseErrorCode("error code!")
                .responseStatus(RESPONSE_STATUS_SUCCESS)
                .build();

        postManualEntryRegisteredLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @ParameterizedTest(name = "Run {index}: referId={0}, leadName={1}, leadPhoneNumber={2}, leadNote={3}," +
            "salesforceId={4}, responseStatus={5}")
    @MethodSource("getInvalidManualEntryRegisteredLeadsParameters")
    @UserStoryId(storyId = {1266})
    void shouldNotReturnManuallyRegisteredLeadsOnInvalidInput(
            String referId, String leadName, String leadPhoneNumber, String leadNote, String salesforceid,
            String responseStatus) {
        val requestObject = RegisterLeadManualEntryModel
                .builder()
                .referId(referId)
                .leadNote(leadNote)
                .responseSalesforceId(salesforceid)
                .responseStatus(responseStatus)
                .build();

        postManualEntryRegisteredLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(storyId = {1209, 1257})
    void shouldReturnManuallyApprovedLeads() {
        ApprovedLeadsManualEntryModel requestObject = ApprovedLeadsManualEntryModel
                .builder()
                .leads(new ApprovedLeadManualEntryModel[]{
                        ApprovedLeadManualEntryModel
                                .builder()
                                .mvnReferralId(customerId)
                                .salesforceId(SALESFORCE_ID)
                                .leadStatus(LeadStatus.CONTACTED)
                                .salesmanSalesforceId(SALESFORCE_ID)
                                .leadAccountSalesforceId(SALESFORCE_ID)
                                .build()
                })
                .build();

        postManualEntryApprovedLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(1266)
    void shouldNotReturnManuallyApprovedLeadsOnNoInternalObjectPassed() {
        ApprovedLeadsManualEntryModel requestObject = ApprovedLeadsManualEntryModel
                .builder()
                .leads(new ApprovedLeadManualEntryModel[]{})
                .build();

        postManualEntryApprovedLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @ParameterizedTest(name = "Run {index}: referId={0}, salesforceId={1}, timestamp={2}, " +
            "fromTimestamp={3}, toTimestamp={4}")
    @MethodSource("getInvalidManualEntryApprovedLeadsParameters")
    @UserStoryId(storyId = {1266})
    void shouldNotReturnManuallyApprovedLeadsOnInvalidInput(
            String referId, String salesforceid, String timestamp, String fromTimestamp, String toTimestamp) {
        ApprovedLeadsManualEntryModel requestObject = ApprovedLeadsManualEntryModel
                .builder()
                .leads(new ApprovedLeadManualEntryModel[]{
                        ApprovedLeadManualEntryModel
                                .builder()
                                .mvnReferralId(referId)
                                .salesforceId(salesforceid)
                                .build()
                })
                .build();

        postManualEntryApprovedLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(storyId = {1209, 1257})
    void shouldReturnManualPropertyPurchasesByLeads() {
        PropertyPurchasesByLeadsManualEntryModel requestObject = PropertyPurchasesByLeadsManualEntryModel
                .builder()
                .propertyPurchasesByLeads(new PropertyPurchaseByLeadManualEntryModel[]{
                        PropertyPurchaseByLeadManualEntryModel
                                .builder()
                                .mvnReferralId(customerId)
                                .timestamp(Instant.now().toString())
                                .vatAmount(SOME_VAT_AMOUNT)
                                .discountAmount(SOME_DISCOUNT_AMOUNT)
                                .netPropertyPrice(SOME_NET_PROPERTY_PRICE)
                                .sellingPropertyPrice(SOME_SELLING_PROPERTY_PRICE)
                                .build()
                })
                .build();

        postManualEntryPropertyPurchasesByLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(1266)
    void shouldNotReturnManualPropertyPurchasesByLeadsOnNoInternalObjectSent() {
        PropertyPurchasesByLeadsManualEntryModel requestObject = PropertyPurchasesByLeadsManualEntryModel
                .builder()
                .propertyPurchasesByLeads(new PropertyPurchaseByLeadManualEntryModel[]{})
                .build();

        postManualEntryPropertyPurchasesByLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @ParameterizedTest(name = "Run {index}: referId={0}, salesforceId={1}, timestamp={2}, " +
            "fromTimestamp={3}, toTimestamp={4}")
    @MethodSource("getInvalidManualEntryPropertyPurchasesByLeadsParameters")
    @UserStoryId(storyId = {1266})
    void shouldNotReturnManualPropertyPurchasesByLeadsOnInvalidInput(
            String referId, String salesforceid, String timestamp, String fromTimestamp, String toTimestamp) {
        PropertyPurchasesByLeadsManualEntryModel requestObject = PropertyPurchasesByLeadsManualEntryModel
                .builder()
                .propertyPurchasesByLeads(new PropertyPurchaseByLeadManualEntryModel[]{
                        PropertyPurchaseByLeadManualEntryModel
                                .builder()
                                .mvnReferralId(referId)
                                .timestamp(timestamp)
                                .build()
                })
                .build();

        postManualEntryPropertyPurchasesByLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(1671)
    void shouldReturnManuallyLeadsChangedSalesmen() {
        val salesforceid = getRandomUuid();
        val requestObject = LeadsChangedSalesmenManualEntryListModel
                .builder()
                .leadsChangedSalesmen(new LeadChangedSalesmanManualEntryModel[]{
                        LeadChangedSalesmanManualEntryModel
                                .builder()
                                .leadSalesforceId(customerId)
                                .salesmanSalesforceId(salesforceid)
                                .timestamp(Instant.now().toString())
                                .build()
                })
                .build();

        postManualEntryLeadsChangedSalesmen(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(1671)
    void shouldNotReturnManuallyLeadsChangedSalesmenOnNoInternalObjectPassed() {
        val requestObject = LeadsChangedSalesmenManualEntryListModel
                .builder()
                .leadsChangedSalesmen(new LeadChangedSalesmanManualEntryModel[]{})
                .build();

        postManualEntryLeadsChangedSalesmen(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @ParameterizedTest(name = "Run {index}: referId={0}, salesforceId={1}, timestamp={2}, " +
            "fromTimestamp={3}, toTimestamp={4}")
    @MethodSource("getInvalidManualEntryApprovedLeadsParameters")
    @UserStoryId(1671)
    void shouldNotReturnManuallyLeadsChangedSalesmenOnInvalidInput(
            String referId, String salesforceid, String timestamp, String fromTimestamp, String toTimestamp) {
        val requestObject = LeadsChangedSalesmenManualEntryListModel
                .builder()
                .leadsChangedSalesmen(new LeadChangedSalesmanManualEntryModel[]{
                        LeadChangedSalesmanManualEntryModel
                                .builder()
                                .leadSalesforceId(referId)
                                .salesmanSalesforceId(salesforceid)
                                .timestamp(Instant.now().toString())
                                .build()
                })
                .build();

        postManualEntryLeadsChangedSalesmen(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @UserStoryId(1671)
    void shouldReturnManuallyAgentsChangedSalesmen() {
        val salesforceid = getRandomUuid();
        val requestObject = AgentsChangedSalesmenManualEntryListModel
                .builder()
                .agentsChangedSalesmen(new AgentChangedSalesmanManualEntryModel[]{
                        AgentChangedSalesmanManualEntryModel
                                .builder()
                                .agentSalesforceId(customerId)
                                .salesmanSalesforceId(salesforceid)
                                .timestamp(Instant.now().toString())
                                .build()
                })
                .build();

        postManualEntryAgentsChangedSalesmen(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    @UserStoryId(1671)
    void shouldReturnAgentsChangedSalesmen() {
        val salesforceid = getRandomUuid();
        val requestObject = GetAgentsChangedSalesmenHistoryRequestModel
                .historyAgentsChangedSalesmenBuilder()
                .fromTimestamp(Instant.now().minus(5, ChronoUnit.MINUTES).toString())
                .toTimestamp(Instant.now().plus(5, ChronoUnit.MINUTES).toString())
                .agentSalesforceId(salesforceid)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(100)
                .build();

        val resultCollection = postHistoryAgentsChangedSalesman(requestObject);

        // TODO: make a valid check (unable for now)
        assertEquals(1, resultCollection.count());
    }

    @Disabled("FAL-1290")
    @ParameterizedTest(name = "Run {index}: referId={0}, leadName={1}, leadPhoneNumber={2}, salesforceId={3}," +
            "fromTimeStamp={4}, toTimestamp={5}, responseStatus={6}")
    @MethodSource("getInvalidHistoryRegisteredLeadsParameters")
    @UserStoryId(storyId = {1210, 1266, 1222})
    void shouldReturnHistoricalRegisteredLeads(
            String referId, String leadName, String leadPhoneNumber, String salesforceId,
            String fromTimeStamp, String toTimestamp, String responseStatus) {
        prepareManualEntryRegisteredLeads(referId, leadName, leadPhoneNumber, salesforceId);
        val requestObject = GetRegisteredLeadsHistoryRequestModel
                .historyRegisteredLeadsBuilder()
                .referId(referId)
                .responseSalesforceId(salesforceId)
                .fromTimestamp(fromTimeStamp)
                .toTimestamp(toTimestamp)
                .responseStatus(responseStatus)
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .build();

        postHistoryRegisteredLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @UserStoryId(storyId = {1120, 1222})
    void shouldReturnHistoricalApprovedLeads() {
        prepareManualEntryRegisteredLeads(customerId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID);
        prepareHistoryRegisteredLoads();
        prepareManualEntryApprovedLeads();
        val requestObject = GetApprovedLeadsHistoryRequestModel
                .historyApprovedLeadsBuilder()
                .referId(customerId)
                .salesforceId(SALESFORCE_ID)
                .salesmanSalesforceId(SALESFORCE_ID)
                .fromTimestamp(Instant.now().minus(5, ChronoUnit.MINUTES).toString())
                .toTimestamp(Instant.now().toString())
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .build();

        val response = postHistoryApprovedLeads(requestObject);
        val paginatedModel = response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedResponseModel.class);
        val actualApprovedLead = (LinkedHashMap) paginatedModel.getData()[0];

        assertAll(
                () -> assertEquals(CURRENT_PAGE, paginatedModel.getCurrentPage()),
                () -> assertEquals(PAGE_SIZE, paginatedModel.getPageSize()),
                () -> assertEquals(customerId, actualApprovedLead.get(REFER_ID_FIELD)),
                () -> assertEquals(SALESFORCE_ID, actualApprovedLead.get(SALESFORCE_ID_FIELD)),
                () -> assertEquals(null, actualApprovedLead.get(SALESMAN_SALESFORCE_ID_FIELD))
        );
    }

    @Test
    @UserStoryId(storyId = {1125, 1222})
    void shouldReturnHistoricalPropertyPurchaesByLeads() {
        prepareManualEntryRegisteredLeads(customerId, VALID_LEAD_NAME, VALID_LEAD_PHONE_NUMBER, SALESFORCE_ID);
        prepareHistoryRegisteredLoads();
        prepareManualEntryApprovedLeads();
        prepareHistoryApprovedLeads();
        prepareManualEntryPropertyPurchasesByLeads();
        val requestObject = GetPropertyPurchasesByLeadsRequestModel
                .historyPropertyPurchasesByLeadsBuilder()
                .referId(customerId)
                .salesforceId(SALESFORCE_ID)
                .fromTimestamp(Instant.now().minus(1, ChronoUnit.MINUTES).toString())
                .toTimestamp(Instant.now().toString())
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .build();

        val response = postHistoryPropertyPurchasesByLeads(requestObject);
        val paginatedModel = response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedResponseModel.class);
        val actualApprovedLead = (LinkedHashMap) paginatedModel.getData()[0];

        assertAll(
                () -> assertEquals(CURRENT_PAGE, paginatedModel.getCurrentPage()),
                () -> assertEquals(PAGE_SIZE, paginatedModel.getPageSize()),
                () -> assertEquals(customerId, actualApprovedLead.get(REFER_ID_FIELD))
                // TODO: investigate into where the salesforceId is from
                // ,
                // () -> assertEquals(SALESFORCE_ID, actualApprovedLead.get(SALESFORCE_ID_FIELD))
        );
    }

    @ParameterizedTest(name = "Run {index}: referId={0}, phoneCountryCode={1}, phoneCountryName={2},"
            + " field={3}, message={4}")
    @MethodSource("getInvalidPhoneCountryCodeAndPhoneCountryName")
    @UserStoryId(storyId = 1738)
    void shouldValidatePhoneCountryCodeAndPhoneCountryNameWhileManuallyRegisteringLeads(String phoneCountryCode,
            String phoneCountryName, String field, String message) {
        val requestObject = RegisterLeadManualEntryModel
                .builder()
                .referId(customerId)
                .email(generateRandomEmail())
                .firstName(VALID_LEAD_NAME)
                .lastName(VALID_LEAD_NAME)
                .phoneNumber(VALID_LEAD_PHONE_NUMBER)
                .phoneCountryCode(phoneCountryCode)
                .phoneCountryName(phoneCountryName)
                .agentSalesforceId(generateRandomString(10))
                .leadNote(VALID_LEAD_NOTE)
                .responseSalesforceId(SALESFORCE_ID)
                .responseErrorCode("error code!")
                .responseStatus(RESPONSE_STATUS_SUCCESS)
                .build();

        postManualEntryRegisteredLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(field, equalTo(message));
    }

    @Test
    @UserStoryId(storyId = {2384, 2385})
    void shouldManualCreatePaidInvoice() {
        val customerId = getRandomUuid();

        val requestObject = ManualPaidInvoicesRequestModel
                .builder()
                .customerId(customerId)
                .invoiceId(getRandomUuid())
                .amount(123)
                .currency(DEFAULT_CURRENCY)
                .responseStatus(RESPONSE_STATUS_SUCCESS)
                .responseErrorCode(null)
                .responsePaymentId(getRandomUuid())
                .build();

        val responseObject = postManualEntryPaidInvoices(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaidInvoicesResponseModel.class);

        assertAll(
                () -> assertEquals(requestObject.getResponsePaymentId(), responseObject.getPaymentId()),
                () -> assertEquals(requestObject.getResponseStatus(), responseObject.getStatus()),
                () -> assertNull(responseObject.getErrorCode())
        );

        val historyRequestObject = HistoryPaidInvoiceRequestModel
                .builder()
                .fromTimestamp(getYesterdayDateString())
                .toTimestamp(getTomorrowDateString())
                .pageSize(10)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .customerId(customerId)
                .build();

        val historyResponseObject = postHistoryPaidInvoices(historyRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(HistoryPaidInvoiceResponseModel.class);

        assertAll(
                () -> assertEquals(requestObject.getCustomerId(), historyResponseObject.getData()[0].getCustomerId()),
                () -> assertEquals(requestObject.getInvoiceId(), historyResponseObject.getData()[0].getInvoiceId()),
                () -> assertEquals(requestObject.getAmount(), historyResponseObject.getData()[0].getAmount()),
                () -> assertEquals(requestObject.getCurrency(), historyResponseObject.getData()[0].getCurrency()),
                () -> assertEquals(requestObject.getResponseStatus(),
                        historyResponseObject.getData()[0].getResponseStatus()),
                () -> assertEquals(requestObject.getResponseErrorCode(),
                        historyResponseObject.getData()[0].getResponseErrorCode()),
                () -> assertEquals(requestObject.getResponsePaymentId(),
                        historyResponseObject.getData()[0].getResponsePaymentId())
        );
    }

    @Test
    @UserStoryId(storyId = {2383, 2385})
    void shouldIntegrationCreatePaidInvoice() {
        val customerId = getRandomUuid();

        val requestObject = IntegrationPaidInvoicesRequestModel
                .builder()
                .customerId(customerId)
                .invoiceId(getRandomUuid())
                .amount(123)
                .currency(DEFAULT_CURRENCY)
                .build();

        val responseObject = postIntegrationPaidInvoices(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaidInvoicesResponseModel.class);

        assertAll(
                () -> assertEquals("Success", responseObject.getStatus()),
                () -> assertNotNull(responseObject.getPaymentId())
        );

        val historyRequestObject = HistoryPaidInvoiceRequestModel
                .builder()
                .fromTimestamp(getYesterdayDateString())
                .toTimestamp(getTomorrowDateString())
                .pageSize(10)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .customerId(customerId)
                .build();

        val historyResponseObject = postHistoryPaidInvoices(historyRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(HistoryPaidInvoiceResponseModel.class);

        assertAll(
                () -> assertEquals(requestObject.getCustomerId(), historyResponseObject.getData()[0].getCustomerId()),
                () -> assertEquals(requestObject.getInvoiceId(), historyResponseObject.getData()[0].getInvoiceId()),
                () -> assertEquals(requestObject.getAmount(), historyResponseObject.getData()[0].getAmount()),
                () -> assertEquals(requestObject.getCurrency(), historyResponseObject.getData()[0].getCurrency()),
                () -> assertEquals(responseObject.getStatus(),
                        historyResponseObject.getData()[0].getResponseStatus()),
                () -> assertEquals(responseObject.getErrorCode(),
                        historyResponseObject.getData()[0].getResponseErrorCode()),
                () -> assertEquals(responseObject.getPaymentId(),
                        historyResponseObject.getData()[0].getResponsePaymentId())
        );
    }

    private void prepareManualEntryRegisteredLeads(
            String referId, String leadName, String leadPhoneNumber, String salesforceId) {
        val requestObject = RegisterLeadManualEntryModel
                .builder()
                .referId(referId)
                .email(generateRandomEmail())
                .firstName(leadName)
                .lastName(leadName)
                .phoneNumber(leadPhoneNumber)
                .phoneCountryCode(VALID_LEAD_PHONE_CODE)
                .phoneCountryName(FakerUtils.country)
                .agentSalesforceId(generateRandomString(10))
                .leadNote(VALID_LEAD_NOTE)
                .responseSalesforceId(salesforceId)
                .responseErrorCode("error code!")
                .responseStatus(RESPONSE_STATUS_SUCCESS)
                .build();

        postManualEntryRegisteredLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    private void prepareHistoryRegisteredLoads() {
        val requestObject = GetRegisteredLeadsHistoryRequestModel
                .historyRegisteredLeadsBuilder()
                .referId(customerId)
                .responseSalesforceId(SALESFORCE_ID)
                .fromTimestamp(Instant.now().minus(1, ChronoUnit.MINUTES).toString())
                .toTimestamp(Instant.now().toString())
                .responseStatus(RESPONSE_STATUS_SUCCESS)
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .build();

        postHistoryRegisteredLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    private void prepareManualEntryApprovedLeads() {
        ApprovedLeadsManualEntryModel requestObject = ApprovedLeadsManualEntryModel
                .builder()
                .leads(new ApprovedLeadManualEntryModel[]{
                        ApprovedLeadManualEntryModel
                                .builder()
                                .mvnReferralId(customerId)
                                .salesforceId(SALESFORCE_ID)
                                .leadStatus(LeadStatus.CONTACTED)
                                .salesmanSalesforceId(SALESFORCE_ID)
                                .leadAccountSalesforceId(getRandomUuid())
                                .build()
                })
                .build();

        postManualEntryApprovedLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    private void prepareHistoryApprovedLeads() {
        val requestObject = GetApprovedLeadsHistoryRequestModel
                .historyApprovedLeadsBuilder()
                .referId(customerId)
                .salesforceId(SALESFORCE_ID)
                .salesmanSalesforceId(SALESFORCE_ID)
                .fromTimestamp(Instant.now().minus(1, ChronoUnit.MINUTES).toString())
                .toTimestamp(Instant.now().toString())
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .build();

        postHistoryApprovedLeads(requestObject);
    }

    private void prepareManualEntryPropertyPurchasesByLeads() {
        PropertyPurchasesByLeadsManualEntryModel requestObject = PropertyPurchasesByLeadsManualEntryModel
                .builder()
                .propertyPurchasesByLeads(new PropertyPurchaseByLeadManualEntryModel[]{
                        PropertyPurchaseByLeadManualEntryModel
                                .builder()
                                .mvnReferralId(customerId)
                                .timestamp(Instant.now().toString())
                                .vatAmount(SOME_VAT_AMOUNT)
                                .discountAmount(SOME_DISCOUNT_AMOUNT)
                                .netPropertyPrice(SOME_NET_PROPERTY_PRICE)
                                .sellingPropertyPrice(SOME_SELLING_PROPERTY_PRICE)
                                .build()
                })
                .build();

        postManualEntryPropertyPurchasesByLeads(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }
}
