package com.lykke.tests.api.service.walletmanagement;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.WALLET_MANAGEMENT_API_TRANSFER_BALANCE_PATH;
import static com.lykke.tests.api.base.Paths.WalletManagement.BLOCK_STATUS_BY_ID_API_PATH;
import static com.lykke.tests.api.base.Paths.WalletManagement.BLOCK_WALLET_API_PATH;
import static com.lykke.tests.api.base.Paths.WalletManagement.UNBLOCK_WALLET_API_PATH;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.walletmanagement.model.BlockUnblockRequestModel;
import com.lykke.tests.api.service.walletmanagement.model.BlockUnblockResponseModel;
import com.lykke.tests.api.service.walletmanagement.model.TransferBalanceRequestModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class WalletManagementUtils {

    public static final String CUSTOMER_ID_FIELD = "CustomerId";
    public static final String ASSET_SYMBOL_FIELD = "AssetSymbol";
    public static final String BALANCE_FIELD = "Balance";
    static final String SENDER_ID_FIELD = "SenderCustomerId";
    static final String OPERATION_ID = "OperationId";
    static final String RECEIVER_ID_FIELD = "ReceiverCustomerId";
    static final String AMOUNT_FIELD = "Amount";

    public static Response balanceTransfer(String senderId, String receiverId, String assetSymbol, Double amount,
            String operationId) {
        return given()
                .contentType(JSON)
                .when()
                .body(createBalanceTransferObject(senderId, receiverId, assetSymbol, amount, operationId))
                .post(WALLET_MANAGEMENT_API_TRANSFER_BALANCE_PATH);
    }

    public Response balanceTransfer(TransferBalanceRequestModel requestObject) {
        return getHeader()
                .when()
                .body(requestObject)
                .post(WALLET_MANAGEMENT_API_TRANSFER_BALANCE_PATH);
    }

    public BlockUnblockResponseModel getWalletBlockStatus(BlockUnblockRequestModel blockUnblockObject) {
        return getHeader()
                .get(BLOCK_STATUS_BY_ID_API_PATH.apply(blockUnblockObject.getCustomerId()))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockUnblockResponseModel.class);
    }

    public BlockUnblockResponseModel blockCustomerWallet(BlockUnblockRequestModel blockUnblockObject) {
        return getHeader()
                .body(blockUnblockObject)
                .post(BLOCK_WALLET_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockUnblockResponseModel.class);
    }

    public BlockUnblockResponseModel unblockCustomerWallet(BlockUnblockRequestModel blockUnblockObject) {
        return getHeader()
                .body(blockUnblockObject)
                .post(UNBLOCK_WALLET_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockUnblockResponseModel.class);
    }

    private static JSONObject createBalanceTransferObject(String senderId, String receiverId, String assetSymbol,
            Double amount, String operationId) {
        JSONObject balanceTransferObject = new JSONObject();
        balanceTransferObject.put(SENDER_ID_FIELD, senderId);
        balanceTransferObject.put(RECEIVER_ID_FIELD, receiverId);
        balanceTransferObject.put(ASSET_SYMBOL_FIELD, assetSymbol);
        balanceTransferObject.put(AMOUNT_FIELD, amount.toString());
        balanceTransferObject.put(OPERATION_ID, operationId);
        return balanceTransferObject;
    }
}
