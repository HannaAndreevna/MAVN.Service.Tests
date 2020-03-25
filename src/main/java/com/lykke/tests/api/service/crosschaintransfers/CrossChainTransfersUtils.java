package com.lykke.tests.api.service.crosschaintransfers;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.CrossChainTransfers.TRANSFER_TO_EXTERNAL_API_PATH;

import com.lykke.tests.api.service.crosschaintransfers.model.TransferToExternalRequest;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CrossChainTransfersUtils {

    public Response postTransferToExternal(TransferToExternalRequest requestModel) {
        return getHeader()
                .body(requestModel)
                .post(TRANSFER_TO_EXTERNAL_API_PATH)
                .thenReturn();
    }
}
