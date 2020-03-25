package com.lykke.tests.api.service.admin.model.blockchain;

import lombok.Builder;
import lombok.Data;

@Data
public class BlockTransactionsByHashRequest extends PaginationModel {

    private String blockHash;

    @Builder(builderMethodName = "requestBuilder")
    public BlockTransactionsByHashRequest(int currentPage, int pageSize, String blockHash) {
        super(currentPage, pageSize);
        this.blockHash = blockHash;
    }
}
