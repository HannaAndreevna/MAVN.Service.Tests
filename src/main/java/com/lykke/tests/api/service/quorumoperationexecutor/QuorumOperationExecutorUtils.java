package com.lykke.tests.api.service.quorumoperationexecutor;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.QuorumOperationExecutor.GET_BALANCE_BY_ADDRESS_API_URL;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.quorumoperationexecutor.model.AddressBalanceResponse;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class QuorumOperationExecutorUtils {

    public Response getBalance(String address) {
        return getHeader()
                .get(GET_BALANCE_BY_ADDRESS_API_URL.apply(address))
                .thenReturn();
    }

    public Double getAddressBalance(String address) {
        val balance = getBalance(address)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AddressBalanceResponse.class)
                .getBalance();

        return Double.valueOf(balance);
    }
}
