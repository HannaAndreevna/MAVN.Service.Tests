package com.lykke.tests.api.service.crosschainwalletlinker;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.CrossChainWalletLinker.CONFIGURATION_API_PATH;
import static com.lykke.tests.api.base.Paths.CrossChainWalletLinker.CONFIGURATION_TYPE_API_PATH;
import static com.lykke.tests.api.base.Paths.CrossChainWalletLinker.CUSTOMER_NEXT_FEE_API_PATH;
import static com.lykke.tests.api.base.Paths.CrossChainWalletLinker.CUSTOMER_PUBLIC_ADDRESS_API_PATH;
import static com.lykke.tests.api.base.Paths.CrossChainWalletLinker.LINK_REQUESTS_API_PATH;
import static com.lykke.tests.api.base.Paths.CrossChainWalletLinker.LINK_REQUEST_APPROVAL_API_PATH;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemRequestModel;
import com.lykke.tests.api.service.crosschainwalletlinker.model.ConfigurationItemType;
import com.lykke.tests.api.service.crosschainwalletlinker.model.LinkApprovalRequestModel;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CrossChainWalletLinkerUtils {

    public Response getCustomerPublicAddress(String customerId) {
        return getHeader()
                .get(CUSTOMER_PUBLIC_ADDRESS_API_PATH.apply(customerId))
                .thenReturn();
    }

    public Response getCustomerNextFee(String customerId) {
        return getHeader()
                .get(CUSTOMER_NEXT_FEE_API_PATH.apply(customerId))
                .thenReturn();
    }

    public Response postLinkRequest(ByCustomerIdRequest requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .post(LINK_REQUESTS_API_PATH)
                .thenReturn();
    }

    public Response postLinkRequestApproval(LinkApprovalRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(LINK_REQUEST_APPROVAL_API_PATH)
                .thenReturn();
    }

    public Response deleteLinkRequest(ByCustomerIdRequest requestModel) {
        return getHeader()
                .queryParams(getQueryParams(requestModel))
                .delete(LINK_REQUESTS_API_PATH)
                .thenReturn();
    }

    public Response getConfigurations() {
        return getHeader()
                .get(CONFIGURATION_API_PATH)
                .thenReturn();
    }

    public Response getConfigurationByType(ConfigurationItemType type) {
        return getHeader()
                .get(CONFIGURATION_TYPE_API_PATH.apply(type.getCode()))
                .thenReturn();
    }

    public Response postOrUpdateConfiguration(ConfigurationItemRequestModel requestModel) {
        return getHeader()
                .body(requestModel)
                .post(CONFIGURATION_API_PATH)
                .thenReturn();
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NetClassName("none")
    public static class ByCustomerIdRequest {

        private String customerId;
    }
}
