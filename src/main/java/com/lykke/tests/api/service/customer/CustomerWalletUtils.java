package com.lykke.tests.api.service.customer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.JsonConversionUtils.convertToJson;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.GET_CUSTOMER_WALLET;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_TRANSFERS_PATH;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_WALLETS_PATH;
import static com.lykke.tests.api.base.Paths.CUSTOMER_API_WALLETS_TRANSFER_PATH;
import static com.lykke.tests.api.base.Paths.Customer.EXTERNAL_TRANSFER_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.GET_CUSTOMER_WALLET_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.LINK_REQUEST_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.NEXT_FEE_API_PATH;
import static com.lykke.tests.api.base.Paths.Customer.TRANSFER_PAYMENT_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.service.customer.model.PaginationRequestModel;
import com.lykke.tests.api.service.customer.model.TransferOperationRequest;
import com.lykke.tests.api.service.customer.model.wallets.ApproveExternalWalletLinkRequest;
import com.lykke.tests.api.service.customer.model.wallets.PaymentTransferRequestModel;
import com.lykke.tests.api.service.customer.model.wallets.TransferToExternalWalletRequest;
import com.lykke.tests.api.service.customer.model.wallets.WalletResponseModel;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerWalletUtils {

    private static final String CURRENT_PAGE_PARAMETER = "CurrentPage";
    private static final String PAGE_SIZE_PARAMETER = "PageSize";

    public static Response transferAsset(String token, String receiverEmail, Double amount, String assetSymbol) {
        return getHeader(token)
                .body(TransferOperationRequest
                        .builder()
                        .receiverEmail(receiverEmail)
                        .amount(amount.toString())
                        .build())
                .post(CUSTOMER_API_WALLETS_TRANSFER_PATH);
    }

    public static Response getTransfers(PaginationRequestModel requestModel, String token) {
        return getHeader(token)
                .queryParams(getQueryParams(requestModel))
                .get(CUSTOMER_API_TRANSFERS_PATH);
    }

    public Response createPaymentTransfer(String token, String campaignId, String invoiceId, Double amount) {
        return getHeader(token)
                .body(convertToJson(
                        PaymentTransferRequestModel.builder()
                                .campaignId(campaignId)
                                .invoiceId(invoiceId)
                                .amount(amount.toString())
                                .build()))
                .post(TRANSFER_PAYMENT_API_PATH)
                .thenReturn();
    }

    public WalletResponseModel[] getCustomerWallets(String token) {
        return getHeader(token)
                .get(GET_CUSTOMER_WALLET_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(WalletResponseModel[].class);
    }

    public Response postLinkRequest(String token) {
        return getHeader(token)
                .post(LINK_REQUEST_API_PATH)
                .thenReturn();
    }

    public Response approveLinkRequest(ApproveExternalWalletLinkRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .put(LINK_REQUEST_API_PATH)
                .thenReturn();
    }

    public Response deleteLinkRequest(String token) {
        return getHeader(token)
                .delete(LINK_REQUEST_API_PATH)
                .thenReturn();
    }

    public Response postExternalTransfer(TransferToExternalWalletRequest requestModel, String token) {
        return getHeader(token)
                .body(requestModel)
                .post(EXTERNAL_TRANSFER_API_PATH)
                .thenReturn();
    }

    public Response getNextFee(String token) {
        return getHeader(token)
                .get(NEXT_FEE_API_PATH)
                .thenReturn();
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String error;
        private String message;
    }
}
