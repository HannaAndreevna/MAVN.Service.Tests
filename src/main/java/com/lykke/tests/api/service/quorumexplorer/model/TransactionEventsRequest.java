package com.lykke.tests.api.service.quorumexplorer.model;

import lombok.Builder;
import lombok.Data;

@Data
public class TransactionEventsRequest extends PaginationModel {

    private String transactionHash;

    @Builder(builderMethodName = "requestBuilder")
    public TransactionEventsRequest(int currentPage, int pageSize, String transactionHash) {
        super(currentPage, pageSize);
        this.transactionHash = transactionHash;
    }
}
