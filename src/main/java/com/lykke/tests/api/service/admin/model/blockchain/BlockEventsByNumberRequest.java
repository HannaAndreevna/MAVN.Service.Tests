package com.lykke.tests.api.service.admin.model.blockchain;

import lombok.Builder;
import lombok.Data;

@Data
public class BlockEventsByNumberRequest extends PaginationModel {

    private long blockNumber;

    @Builder(builderMethodName = "requestBuilder")
    public BlockEventsByNumberRequest(int currentPage, int pageSize, long blockNumber) {
        super(currentPage, pageSize);
        this.blockNumber = blockNumber;
    }
}
