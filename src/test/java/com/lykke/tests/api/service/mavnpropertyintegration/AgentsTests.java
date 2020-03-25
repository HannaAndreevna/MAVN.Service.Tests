package com.lykke.tests.api.service.mavnpropertyintegration;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_MIN;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.getProcessesAgentHistory;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.getRegisteredAgentHistory;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postHistoryAgentsChangedSalesman;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryAgentsChangedSalesmen;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryProcessedAgents;
import static com.lykke.tests.api.service.mavnpropertyintegration.MAVNPropertyIntegrationUtils.postManualEntryRegisteredAgents;
import static com.lykke.tests.api.service.mavnpropertyintegration.model.ProcessedAgentStatus.KYA_APPROVED;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.CommonConsts;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentChangedSalesmanManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentImageModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentRegisterStatus;
import com.lykke.tests.api.service.mavnpropertyintegration.model.AgentsChangedSalesmenManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetAgentsChangedSalesmenHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetProcessedAgentsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.GetRegisteredAgentsHistoryRequestModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ImageDocumentType;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ProcessedAgentManualEntryListModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ProcessedAgentManualEntryModel;
import com.lykke.tests.api.service.mavnpropertyintegration.model.ProcessedAgentStatus;
import com.lykke.tests.api.service.mavnpropertyintegration.model.RegisterAgentManualEntryModel;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AgentsTests extends BaseApiTest {

    private static final int PAGE_SIZE = 100;
    private static final int CURRENT_PAGE = 1;
    private static final String SALESFORCE_ID = "salesforceId";
    private static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static final String SALESFORCE_ID_FIELD = "SalesforceId";
    private static final String SALESMAN_SALESFORCE_ID_FIELD = "SalesmanSalesforceId";
    private static final String AGENT_SALESFORCE_ID_FIELD = "AgentSalesforceId";
    private static final String AGENT_STATUS_FIELD = "AgentStatus";
    private static final String RESPONSE_AGENT_STATUS_FIELD = "ResponseAgentStatus";
    private static final String COUNTRY_OF_RESIDENCE = "USA";

    private static final String ACCOUNT_NUMBER = generateRandomString(18);
    private static final String BANK_ADDRESS = generateRandomString(50);
    private static final String BANK_BRANCH = generateRandomString(20);
    private static final String BANK_NAME = generateRandomString(20);
    private static final String BENEFICIARY_NAME = FakerUtils.fullName;
    private static final String IBAN = "CR23015108410026012345";
    private static final String IMAGE_NAME = generateRandomString(10);
    private static final String IMAGE_CONTENT = "YXNkZmFzZGZhc2RmYXNkZg==";
    private static final String BANK_BRANCH_COUNTRY = "Neverland";

    @ParameterizedTest
    @EnumSource(value = AgentRegisterStatus.class, mode = INCLUDE, names = {"KYA_APPROVED", "KYA_REJECTED"})
    @UserStoryId(storyId = {1535, 1674, 1588, 1340})
    void shouldPostRegisteredAgentsHistory(AgentRegisterStatus agentStatus) {
        var user = new RegistrationRequestModel();
        val email = user.getEmail();
        val customerId = registerCustomer(user);
        val salesforceId = getRandomUuid();
        sendCustomerDataToSalesforce(customerId, email, salesforceId, agentStatus);
        val requestObject = GetRegisteredAgentsHistoryRequestModel
                .registeredAgentsHistoryRequest()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .customerId(customerId)
                .build();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_MIN, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    val actualResultCandidate1 = getRegisteredAgentHistory(requestObject)
                            //.filter(item -> salesforceId.equals(item.get(SALESFORCE_ID_FIELD)))
                            .filter(item -> customerId.equals(item.get(CUSTOMER_ID_FIELD)))
                            .findFirst();
                    val actualResult1 =
                            actualResultCandidate1.isPresent() ? actualResultCandidate1.get() : new LinkedHashMap<>();
                    return customerId.equals(actualResult1.get(CUSTOMER_ID_FIELD));
                });

        val actualResultCandidate = getRegisteredAgentHistory(requestObject)
                .filter(item -> salesforceId.equals(item.get(SALESFORCE_ID_FIELD)))
                .findFirst();
        val actualResult = actualResultCandidate.isPresent() ? actualResultCandidate.get() : new LinkedHashMap<>();

        assertAll(
                () -> assertEquals(customerId, actualResult.get(CUSTOMER_ID_FIELD)),
                () -> assertEquals(agentStatus.getCode(), actualResult.get(RESPONSE_AGENT_STATUS_FIELD)),
                () -> assertEquals(null, actualResult.get(SALESMAN_SALESFORCE_ID_FIELD))
        );
    }

    @ParameterizedTest
    @EnumSource(value = AgentRegisterStatus.class, mode = INCLUDE, names = {"KYA_APPROVED", "KYA_REJECTED"})
    // TODO: rejected -> returns approved, blocked -> returns rejected
    @UserStoryId(storyId = {1535, 1674, 1588, 1340})
    void shouldPostProcessedAgentsHistory(AgentRegisterStatus agentStatus) {
        var user = new RegistrationRequestModel();
        var email = user.getEmail();
        var customerId = registerCustomer(user);
        var salesforceId = getRandomUuid();
        sendAndCheckCustomerDataToSalesforce(customerId, email, salesforceId, agentStatus);
        var timestamp = Instant.now().minus(5, ChronoUnit.DAYS).toString();
        ProcessedAgentManualEntryModel manualEntryRequestObject = ProcessedAgentManualEntryModel
                .builder()
                .timeStamp(Instant.now().toString())
                .salesforceId(salesforceId)
                .agentStatus(ProcessedAgentStatus.valueOf(agentStatus.name()))
                .build();
        sendCustomerDataToManualEntry(manualEntryRequestObject);
        val requestObject = GetProcessedAgentsHistoryRequestModel
                .processedAgentsHistoryRequestBuilder()
                .salesforceId(salesforceId)
                .salesmanSalesforceId(salesforceId)
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .fromTimestamp(timestamp)
                .build();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_MIN, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    val actualResultCandidate1 = getProcessesAgentHistory(requestObject)
                            .filter(item -> salesforceId.equals(item.get(SALESFORCE_ID_FIELD)))
                            .findFirst();
                    val actualResult1 =
                            actualResultCandidate1.isPresent() ? actualResultCandidate1.get() : new LinkedHashMap<>();
                    return !EMPTY.equals(actualResult1.get(SALESFORCE_ID_FIELD));
                });

        val actualResult = getProcessesAgentHistory(requestObject)
                .filter(item -> salesforceId.equals(item.get(SALESFORCE_ID_FIELD)))
                .findFirst()
                .orElse(new LinkedHashMap<>());

        System.out.println(actualResult.getClass());
        assertEquals(salesforceId, actualResult.get(SALESFORCE_ID_FIELD));
        assertEquals(agentStatus.getCode(), actualResult.get(AGENT_STATUS_FIELD));
        assertEquals(null, actualResult.get(SALESMAN_SALESFORCE_ID_FIELD));
    }

    @Test
    @UserStoryId(1672)
    void shouldPostAgentsChangedSalesmen() {
        val agentSalesforceId = getRandomUuid();
        val salesmanSalesforceId = getRandomUuid();

        val manualEntryequestObjet = AgentsChangedSalesmenManualEntryListModel
                .builder()
                .agentsChangedSalesmen(
                        new AgentChangedSalesmanManualEntryModel[]{
                                AgentChangedSalesmanManualEntryModel
                                        .builder()
                                        .agentSalesforceId(agentSalesforceId)
                                        .salesmanSalesforceId(salesmanSalesforceId)
                                        .timestamp(Instant.now().toString())
                                        .build()
                        }
                )
                .build();
        postManualEntryAgentsChangedSalesmen(manualEntryequestObjet);

        val requestObject = GetAgentsChangedSalesmenHistoryRequestModel
                .historyAgentsChangedSalesmenBuilder()
                .fromTimestamp(Instant.now().minus(2, ChronoUnit.MINUTES).toString())
                .toTimestamp(Instant.now().plus(5, ChronoUnit.HOURS).toString())
                .agentSalesforceId(agentSalesforceId)
                .pageSize(100)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .build();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_MIN, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    val actualResultCandidate1 = postHistoryAgentsChangedSalesman(requestObject)
                            .filter(item -> agentSalesforceId.equals(item.get(AGENT_SALESFORCE_ID_FIELD))
                                    || salesmanSalesforceId.equals(item.get(SALESMAN_SALESFORCE_ID_FIELD)))
                            .findFirst();
                    val actualResult1 =
                            actualResultCandidate1.isPresent() ? actualResultCandidate1.get() : new LinkedHashMap<>();
                    return !EMPTY.equals(actualResult1.get(AGENT_SALESFORCE_ID_FIELD)) && !EMPTY
                            .equals(actualResult1.get(SALESMAN_SALESFORCE_ID_FIELD));
                });

        val actualResultCandidate = postHistoryAgentsChangedSalesman(requestObject)
                .filter(item -> agentSalesforceId.equals(item.get(AGENT_SALESFORCE_ID_FIELD)) || salesmanSalesforceId
                        .equals(item.get(SALESMAN_SALESFORCE_ID_FIELD)))
                .findFirst();
        val actualResult = actualResultCandidate.isPresent() ? actualResultCandidate.get() : new LinkedHashMap<>();
        System.out.println(actualResult.getClass());
        assertEquals(agentSalesforceId, actualResult.get(AGENT_SALESFORCE_ID_FIELD));
        assertEquals(salesmanSalesforceId, actualResult.get(SALESMAN_SALESFORCE_ID_FIELD));
        assertEquals(null, actualResult.get(SALESMAN_SALESFORCE_ID_FIELD));
    }

    private void sendCustomerDataToSalesforce(
            String customerId, String email, String salesforceId, AgentRegisterStatus agentStatus) {
        postManualEntryRegisteredAgents(RegisterAgentManualEntryModel
                .builder()
                .customerId(customerId)
                .countryOfResidence(COUNTRY_OF_RESIDENCE)
                .email(email)
                .firstName(FakerUtils.firstName)
                .lastName(FakerUtils.lastName)
                .accountNumber(ACCOUNT_NUMBER)
                .bankAddress(BANK_ADDRESS)
                .bankBranch(BANK_BRANCH)
                .bankBranchCountry(BANK_BRANCH_COUNTRY)
                .bankName(BANK_NAME)
                .beneficiaryName(BENEFICIARY_NAME)
                .iban(IBAN)
                .images(new AgentImageModel[]{AgentImageModel
                        .builder()
                        .imageName(IMAGE_NAME)
                        .documentType(ImageDocumentType.PASSPORT)
                        .imageBase64(IMAGE_CONTENT)
                        .build()
                })
                .phoneCountryName("USA")
                .phoneCountryCode("+1")
                .phoneNumber(FakerUtils.phoneNumber)
                .responseAgentStatus(agentStatus)
                .responseAgentSalesforceId(salesforceId)
                .responseStatus(agentStatus.getCode())
                .responseErrorCode("None")
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    private void sendAndCheckCustomerDataToSalesforce(
            String customerId, String email, String salesforceId, AgentRegisterStatus agentStatus) {
        sendCustomerDataToSalesforce(customerId, email, salesforceId, agentStatus);
        val requestObject = GetRegisteredAgentsHistoryRequestModel
                .registeredAgentsHistoryRequest()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .customerId(customerId)
                .build();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_MIN, TimeUnit.MINUTES)
                .pollInterval(Duration.FIVE_SECONDS)
                .until(() -> {
                    // val result = getRegisteredAgentHistory(requestObject);
                    val actualResultCandidate1 = getRegisteredAgentHistory(requestObject)
                            //.filter(item -> salesforceId.equals(item.get(SALESFORCE_ID_FIELD)))
                            .filter(item -> customerId.equals(item.get(CUSTOMER_ID_FIELD)))
                            .findFirst();
                    val actualResult1 =
                            actualResultCandidate1.isPresent() ? actualResultCandidate1.get() : new LinkedHashMap<>();
                    return customerId.equals(actualResult1.get(CUSTOMER_ID_FIELD));
                    // return customerId.equals(result.get(CUSTOMER_ID_FIELD));
                });
    }

    private void sendCustomerDataToManualEntry(ProcessedAgentManualEntryModel... requestObjectCollection) {
        postManualEntryProcessedAgents(ProcessedAgentManualEntryListModel
                .builder()
                .agents(requestObjectCollection)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);
    }

    // a collection of search parameters
    private Stream<GetRegisteredAgentsHistoryRequestModel> getTestDataForRegisteredAgentsHistory(String customerId,
            String email) {
        return Stream.of(
                GetRegisteredAgentsHistoryRequestModel
                        .registeredAgentsHistoryRequest()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .customerId(customerId)
                        .build(),
                GetRegisteredAgentsHistoryRequestModel
                        .registeredAgentsHistoryRequest()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .customerId(customerId)
                        .email(email)
                        .build(),
                GetRegisteredAgentsHistoryRequestModel
                        .registeredAgentsHistoryRequest()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .customerId(customerId)
                        .email(email)
                        .firstName(FakerUtils.firstName)
                        .build(),
                GetRegisteredAgentsHistoryRequestModel
                        .registeredAgentsHistoryRequest()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .customerId(customerId)
                        .email(email)
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .build(),
                GetRegisteredAgentsHistoryRequestModel
                        .registeredAgentsHistoryRequest()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .customerId(customerId)
                        .email(email)
                        .firstName(FakerUtils.firstName)
                        .lastName(FakerUtils.lastName)
                        .phoneCodeAndNumber(FakerUtils.phoneNumber)
                        .build()
        );
    }

    private Stream<GetProcessedAgentsHistoryRequestModel> getTestDataForProcessedAgentsHistory(String customerId,
            String email) {
        return Stream.of(
                GetProcessedAgentsHistoryRequestModel
                        .processedAgentsHistoryRequestBuilder()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .agentStatus(KYA_APPROVED)
                        .fromTimestamp(Instant.now().minus(5, ChronoUnit.DAYS).toString())
                        .toTimestamp(Instant.now().toString())
                        .build(),
                GetProcessedAgentsHistoryRequestModel
                        .processedAgentsHistoryRequestBuilder()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .agentStatus(KYA_APPROVED)
                        .fromTimestamp(Instant.now().minus(5, ChronoUnit.DAYS).toString())
                        .build(),
                GetProcessedAgentsHistoryRequestModel
                        .processedAgentsHistoryRequestBuilder()
                        .currentPage(CURRENT_PAGE)
                        .pageSize(PAGE_SIZE)
                        .agentStatus(KYA_APPROVED)
                        .build()
        );
    }
}
