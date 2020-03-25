package com.lykke.tests.api.service.quorumexplorer.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;

@Data
@QueryParameters
public class BlockEventsByNumberRequest extends PaginationModel {

    private long blockNumber;

    @Builder(builderMethodName = "requestBuilder")
    public BlockEventsByNumberRequest(int currentPage, int pageSize, long blockNumber) {
        super(currentPage, pageSize);
        this.blockNumber = blockNumber;
    }
}
