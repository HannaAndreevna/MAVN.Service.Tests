package com.lykke.tests.api.service.admin.payments;

import static com.lykke.api.testing.api.common.GenerateUtils.generateRandomPhone;
import static com.lykke.api.testing.api.common.GenerateUtils.getRandomUuid;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_DEFAULT_SEC;
import static com.lykke.tests.api.common.CommonConsts.AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS;
import static com.lykke.tests.api.common.CommonMethods.createDefaultSignUpCampaign;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.prerequisites.BurnRules.createBurnRuleWithENContents;
import static com.lykke.tests.api.service.admin.model.payments.PaymentsUtils.getPagedUnprocessedPaymentsResponse;
import static com.lykke.tests.api.service.customer.CustomerWalletUtils.createPaymentTransfer;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.setPhoneNumber;
import static com.lykke.tests.api.service.privateblockchainfacade.PrivateBlockchainFacadeUtils.getCustomerBalance;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.PagedRequestModel;
import com.lykke.tests.api.service.admin.model.payments.PaymentsUtils;
import com.lykke.tests.api.service.campaigns.model.burnrules.Vertical;
import com.lykke.tests.api.service.currencyconvertor.CurrencyConvertorUtils;
import com.lykke.tests.api.service.currencyconvertor.model.CurrencyRateRequest;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
public class PaymentsTests extends BaseApiTest {

    private static final String ERR_FIELD = "error";
    private static final String NON_EXISTING_CURRENCY_ERR = "CurrencyDoesNotExistInCurrencyConverter";
    private static final String CONVERSION_CURRENCY_01 = "AED";
    private static final String CONVERSION_CURRENCY_02 = "USD";
    private static final Double SOME_AMOUNT = 10.0;

    @Disabled("createBurnRuleWithENContents needs update with verticals ")
    @Test
    @UserStoryId(storyId = 2629)
    void shouldNotAcceptPayment_CurrencyNotExist() {

        createDefaultSignUpCampaign();

        var user = new RegistrationRequestModel();
        val customerId = registerCustomer(user);

        val customerToken = getUserToken(user.getEmail(), user.getPassword());

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Customer's balance: " + getCustomerBalance(customerId));
                    log.info("===============================================================================");
                    return Double.valueOf(10) <= Double.valueOf(getCustomerBalance(customerId));
                });

        val burnRuleId = createBurnRuleWithENContents(false);
        val invoiceId = getRandomUuid();

        createPaymentTransfer(customerToken, burnRuleId, invoiceId, SOME_AMOUNT);

        CurrencyConvertorUtils.deleteCurrenciesByCurrencyCode(CONVERSION_CURRENCY_01, CONVERSION_CURRENCY_02);

        val pagedRequest = PagedRequestModel.builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_LOWER_BOUNDARY)
                .build();

        Awaitility.await().atMost(AWAITILITY_DEFAULT_SEC, TimeUnit.SECONDS)
                .until(() -> PaymentsUtils.getPagedUnprocessedPayments(pagedRequest)
                        .getItems()[0].getInvoiceId().equals(invoiceId));

        val transferId = PaymentsUtils.getPagedUnprocessedPayments(pagedRequest)
                .getItems()[0].getId();

        PaymentsUtils.acceptUnprocessedPayment(transferId)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(ERR_FIELD, equalTo(NON_EXISTING_CURRENCY_ERR));

        val currencyObject = CurrencyRateRequest.builder()
                .baseAsset(CONVERSION_CURRENCY_01)
                .quoteAsset(CONVERSION_CURRENCY_01)
                .rate((float) 2.0)
                .build();

        CurrencyConvertorUtils.createCurrencies(currencyObject);
    }

    @Test
    @UserStoryId(storyId = {3642})
    void shouldGetUnprocessedPaymentsAfterPhoneNumberChanging() {
        createDefaultSignUpCampaign();

        var user = registerDefaultVerifiedCustomer();
        val customerId = user.getCustomerId();
        val customerToken = getUserToken(user.getEmail(), user.getPassword());
        val burnRuleId = createBurnRuleWithENContents(false, Vertical.REALESTATE);
        val invoiceId = getRandomUuid();

        Awaitility.await()
                .atMost(AWAITILITY_INITIAL_BALANCE_WAIT_SECONDS, TimeUnit.SECONDS)
                .pollInterval(Duration.TWO_SECONDS)
                .until(() -> {
                    log.info("===============================================================================");
                    log.info("Customer's balance: " + getCustomerBalance(customerId));
                    log.info("===============================================================================");
                    return Double.valueOf(10) <= Double.valueOf(getCustomerBalance(customerId));
                });

        createPaymentTransfer(customerToken, burnRuleId, invoiceId, SOME_AMOUNT).then()
                .statusCode(SC_NO_CONTENT);

        val pagedRequest = PagedRequestModel.builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_LOWER_BOUNDARY)
                .build();

        getPagedUnprocessedPaymentsResponse(pagedRequest).then().statusCode(SC_OK);

        setPhoneNumber(customerId, customerToken, generateRandomPhone(10),
                1);

        getPagedUnprocessedPaymentsResponse(pagedRequest).then().statusCode(SC_OK);
    }
}
