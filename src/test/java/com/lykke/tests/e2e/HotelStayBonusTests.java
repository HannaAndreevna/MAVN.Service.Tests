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
import com.lykke.tests.api.service.customer.model.partnersmessages.PartnerMessagesResponseModel;
import com.lykke.tests.api.service.customer.model.partnerspayments.ApprovePartnerPaymentRequest;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedPartnerPaymentRequestsResponse;
import com.lykke.tests.api.service.customer.model.partnerspayments.PaginatedRequestModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomerModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomerResponseModel;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomersRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ExecutePaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ExecutePaymentRequestResponseModel;
import com.lykke.tests.api.service.partnermanagement.PartnerManagementUtils.PartnerDto;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostRequestModel;
import com.lykke.tests.api.service.partnersintegration.model.MessagesPostResponseModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestModel;
import com.lykke.tests.api.service.partnerspayments.model.PaymentRequestResponseModel;
import java.time.Instant;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HotelStayBonusTests extends BaseApiTest {

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

    @BeforeEach
    void setUp() {
        val partnerPassword = generateValidPassword();
        val clientId = getRandomUuid();
        partnerData = createPartner(clientId, partnerPassword, generateRandomString(10), generateRandomString(10));
        locationId = getLocationId(partnerData);
        partnerToken = getPartnerToken(clientId, partnerPassword, USER_INFO);
        customerData = createCustomerFundedViaBonusReward(INITIAL_BALANCE);
        customerEmail = customerData.getEmail();
        customerToken = getUserToken(customerData);
        partnerExternalLocationId = getLocationExternalId(partnerData);
        initialBalance = getCustomerBalance(customerToken) + INITIAL_BALANCE;
    }

    @Test
    void shouldGetBonusForHotelStay() {

        // search for a customer
        val customerSearchResult = getCustomersPaginatedResponse(CustomerListRequest
                .builder()
                .currentPage(CURRENT_PAGE)
                .pageSize(PAGE_SIZE)
                .searchValue(customerEmail)
                .build());

        // send a message
        val partnerMessageRequestObject = MessagesPostRequestModel.builder()
                .partnerId(partnerData.getId())
                .customerId(customerSearchResult.getCustomers()[0].getCustomerId())
                .subject(SUBJECT)
                .message(MESSAGE)
                .externalLocationId(partnerExternalLocationId)
                .sendPushNotification(true)
                .build();

        val postMessageResult = postMessage(partnerMessageRequestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(MessagesPostResponseModel.class);

        val getPartnerMessageResult = getPartnerMessageById(postMessageResult.getPartnerMessageId(), customerToken)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PartnerMessagesResponseModel.class);

        assertAll(
                () -> assertEquals(postMessageResult.getPartnerMessageId(),
                        getPartnerMessageResult.getPartnerMessageId()),
                () -> assertEquals(partnerMessageRequestObject.getPartnerId(), getPartnerMessageResult.getPartnerId()),
                () -> assertEquals(partnerMessageRequestObject.getCustomerId(),
                        getPartnerMessageResult.getCustomerId()),
                () -> assertEquals(partnerMessageRequestObject.getSubject(), getPartnerMessageResult.getSubject()),
                () -> assertEquals(partnerMessageRequestObject.getMessage(), getPartnerMessageResult.getMessage())
        );

        /*
        Awaitility.await()
                .ignoreExceptions()
                .atMost(Duration.FIVE_MINUTES)
                .until(() -> false == true);
        */

        // send payment request
        paymentData = postPaymentRequest(getPartnerMessageResult.getPartnerMessageId(), USD_CURRENCY);

        // approve the payment request
        approvePaymentRequest(paymentData.getPaymentRequestId());

        // execute the payment request
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

        val balanceAfterPayment = getCustomerBalance(customerToken);
        assertEquals(initialBalance - PAYMENT, balanceAfterPayment);

        // issue the bonus
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

        val finalBalance = getCustomerBalance(customerToken);

        assertAll(
                () -> assertEquals(initialBalance - PAYMENT, finalBalance)
                // bonus?
        );
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
