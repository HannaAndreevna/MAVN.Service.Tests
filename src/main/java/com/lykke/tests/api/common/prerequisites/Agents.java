package com.lykke.tests.api.common.prerequisites;

import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomEmail;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.service.campaigns.CampaignUtils.deleteCampaign;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.notificationsystembroker.EmailConfirmationUtils.confirmRegistration;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import com.lykke.api.testing.api.common.Base64Utils;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.tests.api.common.HelperUtils;
import com.lykke.tests.api.common.enums.DocumentType;
import com.lykke.tests.api.service.customer.AgentsUtils;
import com.lykke.tests.api.service.customer.model.agents.AgentRegistrationRequestModel;
import com.lykke.tests.api.service.customer.model.agents.BankInfoModel;
import com.lykke.tests.api.service.customer.model.agents.ImageModel;

import java.util.concurrent.TimeUnit;

import com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

@Slf4j
@UtilityClass
public class Agents {

    private final String STATUS = "PendingKya";
    private final String FIRST_NAME = FakerUtils.firstName;
    private final String LAST_NAME = FakerUtils.lastName;
    private final String PHONE_NUMBER = FakerUtils.phoneNumber;
    private final Double REQUIRED_NUMBER_OF_TOKENS = 100.0;
    private final String content = Base64Utils.encodeToString(HelperUtils
            .getImagePath("test_image.jpg"));

    public BankInfoModel.BankInfoModelBuilder baseBankInfoObj = BankInfoModel
            .builder()
            .beneficiaryName(FakerUtils.fullName)
            .bankName(FakerUtils.companyName)
            .bankBranch(FakerUtils.city)
            .accountNumber(FakerUtils.phoneNumber)
            .bankAddress(FakerUtils.address)
            .bankBranchCountryId(1)
            .iban("HU42117730161111101800000000")
            .swift("DABAIE2D");

    public BankInfoModel bankInfoObj = baseBankInfoObj.build();

    public ImageModel.ImageModelBuilder baseImagesObj = ImageModel
            .builder()
            .documentType(DocumentType.PASSPORT.getValue())
            .name(FakerUtils.title)
            .content(content);

    public ImageModel imagesObj = baseImagesObj.build();

    public AgentRegistrationRequestModel.AgentRegistrationRequestModelBuilder agentRequestObject = AgentRegistrationRequestModel
            .builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .phoneNumber(PHONE_NUMBER)
            .countryPhoneCodeId(1)
            .countryOfResidenceId(1)
            .note(FakerUtils.randomQuote)
            .bankInfo(bankInfoObj)
            .images(new ImageModel[]{imagesObj});


    public String createAnAgent() {
        val customerEmail = generateRandomEmail();
        val customerPass = generateValidPassword();
        return createAnAgent(customerEmail, customerPass);
    }

    public String createAnAgent(String emailAddress, String password) {
        val earnRuleId = EarnRules.createBasicSignUpEarnRule();
        val customerId = RegisterCustomerUtils.registerCustomer();

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Customer's balance: " + getCustomerBalance(customerId));
                    log.info("===============================================================================");
                    return REQUIRED_NUMBER_OF_TOKENS <= Double.valueOf(getCustomerBalance(customerId));
                });
        val customerToken = getUserToken(emailAddress, password);

        confirmRegistration(emailAddress, password);

        AgentsUtils.createAgent(agentRequestObject.token(customerToken).build())
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        Awaitility.await()
                .atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Customer's status " + AgentsUtils.getAgents(customerToken).getStatus());
                    log.info("===============================================================================");
                    return STATUS.equals(AgentsUtils.getAgents(customerToken).getStatus());
                });

        //delete the campaign we created at the beginning.
        deleteCampaign(earnRuleId);

        return customerToken;
    }
}
