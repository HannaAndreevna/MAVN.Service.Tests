package com.lykke.tests.api.service.customermanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.BLOCK_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.BLOCK_STATUS_LIST_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.BLOCK_STATUS_PATH;
import static com.lykke.tests.api.base.PathConsts.CustomerManagementEndpoint.UNBLOCK_PATH;
import static com.lykke.tests.api.base.Paths.CustomerManagement.CUSTOMERS_API_PATH;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.customermanagement.model.blockeduser.CustomerBlockRequest;
import com.lykke.tests.api.service.customermanagement.model.blockeduser.ErrorResponseModel;
import com.lykke.tests.api.service.customermanagement.model.blockeduser.StatusResponseModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlockCustomerUtils {

    private static final String CUSTOMER_ID_FIELD = "CustomerId";
    private static final String CUSTOMER_IDS_FIELD = "CustomerIds";

    @Step("Block customer")
    public ErrorResponseModel blockCustomer(CustomerBlockRequest customerBlockRequest) {
        return getHeader()
                .body(customerIdObject(customerBlockRequest))
                .post(CUSTOMERS_API_PATH + BLOCK_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Unblock customer")
    public ErrorResponseModel unblockCustomer(CustomerBlockRequest customerBlockRequest) {
        return getHeader()
                .body(customerIdObject(customerBlockRequest))
                .post(CUSTOMERS_API_PATH + UNBLOCK_PATH.getPath())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Get block status for customer {customerId}")
    public StatusResponseModel getBlockStatus(String customerId) {
        return getHeader()
                .get(CUSTOMERS_API_PATH + BLOCK_STATUS_PATH.getFilledInPath(customerId))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(StatusResponseModel.class);
    }

    public Response getBlockStatusList(String[] customerIds) {
        return getHeader()
                .body(blockStatusListObject(customerIds))
                .post(CUSTOMERS_API_PATH + BLOCK_STATUS_LIST_PATH.getPath());
    }

    private Map<String, String[]> blockStatusListObject(String[] customerIds) {
        Map<String, String[]> blockStatusList = new HashMap<String, String[]>();
        blockStatusList.put(CUSTOMER_IDS_FIELD, customerIds);
        return blockStatusList;
    }

    private Map<String, String> customerIdObject(CustomerBlockRequest customerBlockRequest) {
        return Stream.of(new String[][]{
                {CUSTOMER_ID_FIELD, customerBlockRequest.getCustomerId()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }
}
