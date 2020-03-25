package com.lykke.tests.api.service.partnerapi;

import static com.lykke.tests.api.base.Paths.PartnerApi.BONUS_CUSTOMERS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.CUSTOMERS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.CUSTOMER_BALANCE_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.CUSTOMER_BALANCE_QUERY_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.LOGIN_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.LOGOUT_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.MESSAGES_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.PAYMENTS_REQUESTS_API_PATH;
import static com.lykke.tests.api.base.Paths.PartnerApi.REFERRALS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.base.Paths.PartnerApi;
import com.lykke.tests.api.service.partnerapi.model.BonusCustomersRequestModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerBalanceRequestModel;
import com.lykke.tests.api.service.partnerapi.model.CustomerInformationRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ExecutePaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.GetPaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.LoginRequestModel;
import com.lykke.tests.api.service.partnerapi.model.LoginResponseModel;
import com.lykke.tests.api.service.partnerapi.model.CreatePaymentRequestRequestModel;
import com.lykke.tests.api.service.partnerapi.model.ReferralInformationRequestModel;
import com.lykke.tests.api.service.partnerapi.model.SendMessageRequestModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PartnerApiUtils {

    @Step("Log in partner")
    public Response loginPartner(LoginRequestModel requestObject) {
        return getHeader()
                .body(requestObject)
                .post(LOGIN_API_PATH)
                .thenReturn();
    }

    @Step("Get partner token by partner id {partnerId}")
    public String getPartnerToken(String partnerId, String partnerPassword, String userInfo) {
        return loginPartner(LoginRequestModel
                .builder()
                .clientId(partnerId)
                .clientSecret(partnerPassword)
                .userInfo(userInfo)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(LoginResponseModel.class)
                .getToken();
    }

    public Response logOutPartner(String token) {
        return getHeader(token)
                .post(LOGOUT_API_PATH)
                .thenReturn();
    }

    @Step("Get customer info")
    public Response getCustomerInfo(CustomerInformationRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMERS_API_PATH)
                .thenReturn();
    }

    @Deprecated
    @Step("Get customer balance")
    public Response getCustomerBalance_Deprecated(CustomerBalanceRequestModel requestModel, String customerId,
            String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_BALANCE_API_PATH.apply(customerId))
                .thenReturn();
    }

    @Step("Get customer balance")
    public Response getCustomerBalance(CustomerBalanceRequestModel requestModel, String customerId, String token) {
        return getHeader(token)
                .queryParam("customerId", customerId)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_BALANCE_QUERY_API_PATH)
                .thenReturn();
    }

    @Step("Get referral information")
    public Response getReferralInformation(ReferralInformationRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(REFERRALS_API_PATH)
                .thenReturn();
    }

    public Response postTriggerBonusToCustomer(BonusCustomersRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(BONUS_CUSTOMERS_API_PATH)
                .thenReturn();
    }

    @Step
    public <E extends CreatePaymentRequestRequestModel> Response postPaymentRequest(E requestModel, String token) {
        val body = getQueryParams(requestModel,
                x -> !EMPTY.equalsIgnoreCase(x) && !"0.0".equalsIgnoreCase(x) && !"0".equalsIgnoreCase(x));

        return getHeader(token)
                .body(body)
                .post(PAYMENTS_REQUESTS_API_PATH)
                .thenReturn();
    }

    @Step("Get payment by request id")
    public Response getPaymentByRequestId(GetPaymentRequestRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(PAYMENTS_REQUESTS_API_PATH)
                .thenReturn();
    }

    @Step("Cancel payment by request id")
    public Response cancelPaymentByRequestId(GetPaymentRequestRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .delete(PAYMENTS_REQUESTS_API_PATH)
                .thenReturn();
    }

    public Response executePayment(ExecutePaymentRequestRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(PartnerApi.PAYMENTS_API_PATH)
                .thenReturn();
    }

    Response postMessage(SendMessageRequestModel requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(MESSAGES_API_PATH)
                .thenReturn();
    }
}
