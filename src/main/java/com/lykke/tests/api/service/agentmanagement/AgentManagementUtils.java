package com.lykke.tests.api.service.agentmanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.FakerUtils.fullName;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.AgentManagement.AGENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.AgentManagement.AGENTS_LIST_API_PATH;
import static com.lykke.tests.api.base.Paths.AgentManagement.REQUIREMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.AgentManagement.REQUIREMENTS_TOKENS_API_PATH;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.agentmanagement.model.DocumentType.PASSPORT;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.common.model.ByCustomerIdRequestModel;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.agentmanagement.model.BankInfoModel;
import com.lykke.tests.api.service.agentmanagement.model.ImageModel;
import com.lykke.tests.api.service.agentmanagement.model.RegistrationModel;
import com.lykke.tests.api.service.agentmanagement.model.RegistrationResultModel;
import com.lykke.tests.api.service.agentmanagement.model.UpdateTokensRequirementModel;
import com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class AgentManagementUtils {

    public static final String ACCOUNT_NUMBER = generateRandomString(18);
    public static final String BANK_ADDRESS = generateRandomString(50);
    public static final String BANK_BRANCH = generateRandomString(20);
    public static final String BANK_NAME = generateRandomString(20);
    public static final String BENEFICIARY_NAME = fullName;
    public static final String IBAN = "CR23015108410026012345";
    public static final String IMAGE_NAME = generateRandomString(10);
    public static final String IMAGE_CONTENT = "YXNkZmFzZGZhc2RmYXNkZg==";
    public static final int COUNTRY_OF_RESIDENCE_ID = 1;
    public static final int COUNTRY_PHONE_CODE_ID = 1;

    public Response getAgent(ByCustomerIdRequestModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(AGENTS_API_PATH)
                .thenReturn();
    }

    public Response postAgent(RegistrationModel requestObject) {
        return getHeader(getAdminToken())
                .body(requestObject)
                .post(AGENTS_API_PATH)
                .thenReturn();
    }

    Response getListOfAgents(String[] customerIds) {
        return getHeader(getAdminToken())
                .body(customerIds)
                .post(AGENTS_LIST_API_PATH)
                .thenReturn();
    }

    public Response getRequirements(ByCustomerIdRequestModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(REQUIREMENTS_API_PATH)
                .thenReturn();
    }

    public Response getRequirementsTokens() {
        return getHeader(getAdminToken())
                .get(REQUIREMENTS_TOKENS_API_PATH)
                .thenReturn();
    }

    public Response updateRequirementsTokens(UpdateTokensRequirementModel requestModel) {
        return getHeader(getAdminToken())
                .body(requestModel)
                .put(REQUIREMENTS_TOKENS_API_PATH)
                .thenReturn();
    }

    public CustomerBalanceInfo registerDefaultAgent() {
        val customerData = PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward(100.0, true);
        postAgent(RegistrationModel
                .builder()
                .customerId(customerData.getCustomerId())
                .firstName(customerData.getFirstName())
                .lastName(customerData.getLastName())
                .phoneNumber(customerData.getPhoneNumber())
                .bankInfo(BankInfoModel
                        .builder()
                        .accountNumber(ACCOUNT_NUMBER)
                        .bankAddress(BANK_ADDRESS)
                        .bankBranch(BANK_BRANCH)
                        .bankBranchCountryId(1)
                        .bankName(BANK_NAME)
                        .beneficiaryName(BENEFICIARY_NAME)
                        .iban(IBAN)
                        .build())
                .images(new ImageModel[]{ImageModel
                        .builder()
                        .name(IMAGE_NAME)
                        .documentType(PASSPORT)
                        .content(IMAGE_CONTENT)
                        .build()})
                .countryOfResidenceId(COUNTRY_OF_RESIDENCE_ID)
                .countryPhoneCodeId(COUNTRY_PHONE_CODE_ID)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(RegistrationResultModel.class);
        return customerData;
    }
}
