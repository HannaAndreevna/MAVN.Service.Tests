package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.base.Paths.AdminApi.BLOCKS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.BLOCK_BY_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.BLOCK_BY_NUMBER_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.EVENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.EVENTS_BY_BLOCK_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.EVENTS_BY_BLOCK_NUMBER_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.TRANSACTIONS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.TRANSACTIONS_BY_BLOCK_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.TRANSACTIONS_BY_BLOCK_NUMBER_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.TRANSACTION_DETAILS_BY_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.TRANSACTION_EVENTS_API_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.admin.model.blockchain.BlockListRequest;
import com.lykke.tests.api.service.admin.model.blockchain.BlockListResponse;
import com.lykke.tests.api.service.admin.model.blockchain.BlockModel;
import com.lykke.tests.api.service.admin.model.blockchain.EventListRequest;
import com.lykke.tests.api.service.admin.model.blockchain.EventListResponse;
import com.lykke.tests.api.service.admin.model.blockchain.EventModel;
import com.lykke.tests.api.service.admin.model.blockchain.PaginationModel;
import com.lykke.tests.api.service.admin.model.blockchain.TransactionListRequest;
import com.lykke.tests.api.service.admin.model.blockchain.TransactionListResponse;
import com.lykke.tests.api.service.admin.model.blockchain.TransactionModel;
import io.restassured.response.Response;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

@UtilityClass
public class BlockchainUtils {

    public Response getBlocks(BlockListRequest requestModel) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestModel))
                .get(BLOCKS_API_PATH)
                .thenReturn();
    }

    public BlockModel getFirstAvailableBlock() {
        val requestObject = new BlockListRequest(PAGE_SIZE_UPPER_BOUNDARY, CURRENT_PAGE_LOWER_BOUNDARY);
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(BLOCKS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockListResponse.class)
                .getBlocks()[0];
    }

    public BlockModel getFirstAvailableBlockWithTransactions() {
        var result = new BlockModel();
        for (int currentPage = CURRENT_PAGE_LOWER_BOUNDARY; currentPage <= CURRENT_PAGE_UPPER_BOUNDARY; currentPage++) {
            val requestObject = new BlockListRequest(PAGE_SIZE_UPPER_BOUNDARY, currentPage);
            val blocks = getHeader(getAdminToken())
                    .queryParams(getQueryParams(requestObject))
                    .get(BLOCKS_API_PATH)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(BlockListResponse.class)
                    .getBlocks();
            result = Arrays.stream(blocks)
                    .filter(block -> 0 < block.getTransactionsCount())
                    .findFirst()
                    .orElse(new BlockModel());
            if (0 != result.getBlockNumber()) {
                return result;
            }
        }
        return result;
    }

    public BlockModel getFirstAvailableBlockWithEvents() {
        var result = new BlockModel();
        for (int currentPage = CURRENT_PAGE_LOWER_BOUNDARY; currentPage <= CURRENT_PAGE_UPPER_BOUNDARY; currentPage++) {
            val requestObject = new BlockListRequest(PAGE_SIZE_UPPER_BOUNDARY, currentPage);
            val blocks = getHeader(getAdminToken())
                    .queryParams(getQueryParams(requestObject))
                    .get(BLOCKS_API_PATH)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(BlockListResponse.class)
                    .getBlocks();
            result = Arrays.stream(blocks)
                    .filter(block -> 0 < block.getTransactionsCount()
                            && 0 < getEventsByBlockNumber(block.getBlockNumber(),
                            PaginationModel
                                    .builder()
                                    .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                    .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                                    .build())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(EventListResponse.class)
                            .getEvents().length)
                    .findFirst()
                    .orElse(new BlockModel());
            if (0 != result.getBlockNumber()) {
                return result;
            }
        }

        return result;
    }

    @Deprecated
    public Response getBlockByNumber(long blockNumber) {
        return getHeader(getAdminToken())
                .get(BLOCK_BY_NUMBER_API_PATH.apply(blockNumber))
                .thenReturn();
    }

    @Deprecated
    public Response getBlockByHash(String blockHash) {
        return getHeader(getAdminToken())
                .get(BLOCK_BY_HASH_API_PATH.apply(blockHash))
                .thenReturn();
    }

    public Response getTransations(TransactionListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(TRANSACTIONS_API_PATH)
                .thenReturn();
    }

    @Deprecated
    public Response getTransactionsByBlockNumber(long blockNumber, PaginationModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(TRANSACTIONS_BY_BLOCK_NUMBER_API_PATH.apply(blockNumber))
                .thenReturn();
    }

    @Deprecated
    public Response getTransactionsByBlockHash(String blockHash,
            PaginationModel requestModel) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestModel))
                .get(TRANSACTIONS_BY_BLOCK_HASH_API_PATH.apply(blockHash))
                .thenReturn();
    }

    public Response getEvents(EventListRequest requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(EVENTS_API_PATH)
                .thenReturn();
    }

    @Deprecated
    public Response getEventsByBlockNumber(long blockNumber, PaginationModel requestModel) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestModel))
                .get(EVENTS_BY_BLOCK_NUMBER_API_PATH.apply(blockNumber))
                .thenReturn();
    }

    @Deprecated
    public Response getEventsByBlockHash(String blockHash, PaginationModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(EVENTS_BY_BLOCK_HASH_API_PATH.apply(blockHash))
                .thenReturn();
    }

    public TransactionModel getFirstAvailableTransaction() {
        val blockNumber = getFirstAvailableBlockWithTransactions().getBlockNumber();

        return getTransactionsByBlockNumber(blockNumber, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionListResponse.class)
                .getTransactions()[0];
    }

    public EventModel getFirstAvailableEvent() {
        val blockNumber = getFirstAvailableBlockWithEvents().getBlockNumber();

        return getEventsByBlockNumber(blockNumber, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EventListResponse.class)
                .getEvents()[0];
    }

    public Response getTransactionEvents(String hash, PaginationModel requestObject) {
        return getHeader(getAdminToken())
                .queryParams(getQueryParams(requestObject))
                .get(TRANSACTION_EVENTS_API_PATH.apply(hash))
                .thenReturn();
    }

    @Deprecated
    public Response getTransactionDetailedInfoByTransactionHash(String transactionHash) {
        return getHeader(getAdminToken())
                .get(TRANSACTION_DETAILS_BY_HASH_API_PATH.apply(transactionHash))
                .thenReturn();
    }
}
