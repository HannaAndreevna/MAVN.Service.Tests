package com.lykke.tests.api.service.ethereumbridge;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.EthereumBridge.BALANCE_BY_WALLET_ADDRESS_API_PATH;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.ethereumbridge.model.BalanceModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExternalWalletUtils {

    public BalanceModel getExternalBalance(String walletAddress) {
        return getHeader()
                .get(BALANCE_BY_WALLET_ADDRESS_API_PATH.apply(walletAddress))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BalanceModel.class);
    }
}
