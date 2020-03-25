package com.lykke.tests.api.service.quorumexplorer.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.Builder;
import lombok.Data;

@Data
@QueryParameters
public class BlockEventsByHashRequest extends PaginationModel {

    private String blockHash;

    @Builder(builderMethodName = "requestBuilder")
    public BlockEventsByHashRequest(int currentPage, int pageSize, String blockHash) {
        super(currentPage, pageSize);
        this.blockHash = blockHash;
    }
}
