package com.lykke.tests.e2e;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomString;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.api.testing.api.common.PasswordGen.generateValidPassword;
import static com.lykke.tests.api.common.CommonConsts.Currency.USD_CURRENCY;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.admin.GetCustomersUtils.getCustomersPaginatedResponse;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.getCustomerWallets;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customer.PartnersMessagesUtils.getPartnerMessageById;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.approvePayment;
import static com.lykke.tests.api.service.customer.PartnersPaymentsUtils.getSucceededPayments;
import static com.lykke.tests.api.service.partnerapi.PartnerApiLogInLogOutTests.USER_INFO;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.executePayment;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.getPartnerToken;
import static com.lykke.tests.api.service.partnerapi.PartnerApiUtils.postTriggerBonusToCustomer;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.createPartner;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationExternalId;
import static com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.getLocationId;
import static com.lykke.tests.api.service.partnersintegration.PartnersIntegrationUtils.postMessage;
import static com.lykke.tests.api.service.partnerspayments.PartnersPaymentsUtils.postPayment;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.createCustomerFundedViaBonusReward;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.CustomerBalanceInfo;
import com.lykke.tests.api.service.admin.model.CustomerListRequest;
import com.lykke.tests.api.service.admin.model.CustomerListResponse;
import com.lykke.tests.api.service.customer.model.partnersmessages.PartnerMessagesResponseModel;
import com.lykke.tests.api.service.customer.model.partnerspayments.ApprovePartnerPaymentRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedPartnerPaymentRequestsResponse;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedRequestModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomerModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomerResponseModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomersRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ExecutePaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ExecutePaymentRequestResponseModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostResponseModel;
import com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.PartnerDto;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestResponseModel;
import cucumber.api.java8.En;
import java.time.Instant;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;

public class HotelStayBonusStepsDef extends BaseApiTest implements En {

    private static final int CURRENT_PAGE = 1;
    private static final int PAGE_SIZE = 500;
    private static final String SUBJECT = "Subject: hello";
    private static final String MESSAGE = "Message: hello";
    private static final Double INITIAL_BALANCE = 1000.0;
    private static final Double PAYMENT = 200.0;
    private PaymentRequestResponseModel paymentData;
    private CustomerBalanceInfo customerData;
    private PartnerDto partnerData;
    private String customerEmail;
    private String customerToken;
    private String partnerExternalLocationId;
    private String locationId;
    private String partnerToken;
    private Double initialBalance;
    private CustomerListResponse customerSearchResult;
    private MessagesPostResponseModel postMessageResult;
    private MessagesPostRequestModel partnerMessageRequestObject;
    private PartnerMessagesResponseModel getPartnerMessageResult;

    public HotelStayBonusStepsDef() {
        Given("^there is a partner$", () -> {
            val partnerPassword = generateValidPassword();
            val clientId = getRandomUuid();
            partnerData = createPartner(clientId, partnerPassword, generateRandomString(10), generateRandomString(10));
            locationId = getLocationId(partnerData);
            partnerToken = getPartnerToken(clientId, partnerPassword, USER_INFO);
            partnerExternalLocationId = getLocationExternalId(partnerData);
        });
        And("^there is a customer$", () -> {
            customerData = createCustomerFundedViaBonusReward(INITIAL_BALANCE);
            customerEmail = customerData.getEmail();
            customerToken = getUserToken(customerData);
            initialBalance = getCustomerBalance(customerToken) + INITIAL_BALANCE;
        });
        Given("^partner searches for the customer$", () -> {
            customerSearchResult = getCustomersPaginatedResponse(CustomerListRequest
                    .builder()
                    .currentPage(CURRENT_PAGE)
                    .pageSize(PAGE_SIZE)
                    .searchValue(customerEmail)
                    .build());
        });
        And("^sends a message$", () -> {
            partnerMessageRequestObject = MessagesPostRequestModel.builder()
                    .partnerId(partnerData.getId())
                    .customerId(customerSearchResult.getCustomers()[0].getCustomerId())
                    .subject(SUBJECT)
                    .message(MESSAGE)
                    .externalLocationId(partnerExternalLocationId)
                    .sendPushNotification(true)
                    .build();

            postMessageResult = postMessage(partnerMessageRequestObject)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(MessagesPostResponseModel.class);
        });
        And("^customer receives the message$", () -> {
            getPartnerMessageResult = getPartnerMessageById(postMessageResult.getPartnerMessageId(), customerToken)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(PartnerMessagesResponseModel.class);

            assertAll(
                    () -> assertEquals(postMessageResult.getPartnerMessageId(),
                            getPartnerMessageResult.getPartnerMessageId()),
                    () -> assertEquals(partnerMessageRequestObject.getPartnerId(),
                            getPartnerMessageResult.getPartnerId()),
                    () -> assertEquals(partnerMessageRequestObject.getCustomerId(),
                            getPartnerMessageResult.getCustomerId()),
                    () -> assertEquals(partnerMessageRequestObject.getSubject(), getPartnerMessageResult.getSubject()),
                    () -> assertEquals(partnerMessageRequestObject.getMessage(), getPartnerMessageResult.getMessage())
            );
        });
        When("^partner creates a payment request$",
                () -> paymentData = postPaymentRequest(getPartnerMessageResult.getPartnerMessageId(), USD_CURRENCY));
        And("^customer approves the payment request$", () -> approvePaymentRequest(paymentData.getPaymentRequestId()));
        And("^partner executes the request$", () -> {
            executePayment(ExecutePaymentRequestRequestModel
                    .builder()
                    .paymentRequestId(getPartnerMessageResult.getPartnerMessageId())
                    .build(), partnerToken)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(ExecutePaymentRequestResponseModel.class);

            Awaitility.await()
                    .atMost(Duration.TWO_MINUTES)
                    .pollInterval(Duration.TWO_SECONDS)
                    .until(() -> initialBalance - PAYMENT == getCustomerBalance(customerToken));
        });
        Then("^balance is as expected$", () -> {
            val balanceAfterPayment = getCustomerBalance(customerToken);
            assertEquals(initialBalance - PAYMENT, balanceAfterPayment);
        });
        When("^partner triggers a bonus$", () -> {
            val expectedBonusResult = BonusCustomerResponseModel
                    .builder()
                    .customerId(customerData.getCustomerId())
                    .customerEmail(customerEmail)
                    .bonusCustomerSeqNumber(1)
                    .build();

            val actualBonusResult = postTriggerBonusToCustomer(BonusCustomersRequestModel
                    .builder()
                    .bonusCustomers(new BonusCustomerModel[]{
                            new BonusCustomerModel(customerData.getCustomerId(), customerEmail, PAYMENT / 10,
                                    USD_CURRENCY,
                                    Instant.now().toString(),
                                    partnerData.getId(), locationId, generateRandomString(10))})
                    .build(), partnerToken)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(BonusCustomerResponseModel[].class)[0];

            assertEquals(expectedBonusResult, actualBonusResult);
        });
        Then("^balance reflects the bonus arrived$", () -> {
            val finalBalance = getCustomerBalance(customerToken);

            assertAll(
                    () -> assertEquals(initialBalance - PAYMENT, finalBalance)
                    // bonus?
            );
        });
    }

    private Double getCustomerBalance(String token) {
        return Double.valueOf(getCustomerWallets(token)[0].getBalance().replace(",", ""));
    }

    private PaymentRequestResponseModel postPaymentRequest(String messageId, String currency) {
        return postPayment(PaymentRequestModel
                .builder()
                .currency(currency)
                .customerId(customerData.getCustomerId())
                .partnerId(partnerData.getId())
                .tokensAmount(PAYMENT.toString())
                .locationId(locationId)
                .partnerMessageId(messageId)
                .totalBillAmount(PAYMENT * 5)
                .build(), partnerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaymentRequestResponseModel.class);
    }

    private void approvePaymentRequest(String paymentRequestId) {
        approvePayment(ApprovePartnerPaymentRequest
                .builder()
                .paymentRequestId(paymentRequestId)
                .sendingAmount(PAYMENT.toString())
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        getSucceededPayments(PaginatedRequestModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedPartnerPaymentRequestsResponse.class);
    }
}
