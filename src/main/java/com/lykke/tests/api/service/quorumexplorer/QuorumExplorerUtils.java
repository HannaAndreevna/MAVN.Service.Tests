package com.lykke.tests.api.service.quorumexplorer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.BLOCKS_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.BLOCK_BY_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.BLOCK_BY_NUMBER_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.EVENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.EVENTS_BY_BLOCK_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.EVENTS_BY_BLOCK_NUMBER_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.TRANSACTIONS_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.TRANSACTIONS_BY_BLOCK_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.TRANSACTIONS_BY_BLOCK_NUMBER_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.TRANSACTION_DETAILS_BY_HASH_API_PATH;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.TRANSACTION_EVENTS_API_PATH;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.quorumexplorer.model.Block;
import com.lykke.tests.api.service.quorumexplorer.model.BlockEventsByHashRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlockEventsByNumberRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlockTransactionsByHashRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlockTransactionsByNumberRequest;
import com.lykke.tests.api.service.quorumexplorer.model.Event;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredEventsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredTransactionsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedBlocksResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedEventsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedTransactionsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginationModel;
import com.lykke.tests.api.service.quorumexplorer.model.Transaction;
import com.lykke.tests.api.service.quorumexplorer.model.TransactionEventsRequest;
import io.restassured.response.Response;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

@UtilityClass
public class QuorumExplorerUtils {

    public Response getBlocks(PaginationModel requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(BLOCKS_API_PATH)
                .thenReturn();
    }

    public Block getFirstAvailableBlock() {
        val requestObject = new PaginationModel(CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY);
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(BLOCKS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedBlocksResponse.class)
                .getBlocks()[0];
    }

    public Block getFirstAvailableBlockWithTransactions() {
        Block result = new Block();
        for (int currentPage = CURRENT_PAGE_LOWER_BOUNDARY; currentPage <= CURRENT_PAGE_UPPER_BOUNDARY; currentPage++) {
            val requestObject = new PaginationModel(currentPage, PAGE_SIZE_UPPER_BOUNDARY);
            val blocks = getHeader()
                    .queryParams(getQueryParams(requestObject))
                    .get(BLOCKS_API_PATH)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(PaginatedBlocksResponse.class)
                    .getBlocks();
            var resultCandidate = Arrays.stream(blocks)
                    .filter(block -> 0 < block.getTransactionsCount())
                    .findFirst();
            if (resultCandidate.isPresent()) {
                result = resultCandidate.get();
                break;
            }
        }
        return result;
    }

    public Block getFirstAvailableBlockWithEvents() {
        return getFirstAvailableBlockWithEvents(1);
    }

    public Block getFirstAvailableBlockWithEvents(int numberOfEvents) {
        Block result = new Block();
        for (int currentPage = CURRENT_PAGE_LOWER_BOUNDARY; currentPage <= CURRENT_PAGE_UPPER_BOUNDARY; currentPage++) {
            val requestObject = new PaginationModel(currentPage, PAGE_SIZE_UPPER_BOUNDARY);
            val blocks = getHeader()
                    .queryParams(getQueryParams(requestObject))
                    .get(BLOCKS_API_PATH)
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
                    .extract()
                    .as(PaginatedBlocksResponse.class)
                    .getBlocks();
            var resultCandidate = Arrays.stream(blocks)
                    .filter(block -> 0 < block.getTransactionsCount()
                            && numberOfEvents <= getEventsByBlockNumber(BlockEventsByNumberRequest
                            .requestBuilder()
                            .blockNumber(block.getBlockNumber())
                            .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                            .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                            .build())
                            .then()
                            .assertThat()
                            .statusCode(SC_OK)
                            .extract()
                            .as(PaginatedTransactionsResponse.class)
                            .getTotalCount())
                    .findFirst();
            if (resultCandidate.isPresent()) {
                result = resultCandidate.get();
                break;
            }
        }
        return result;
    }

    public Response getBlockByNumber(long blockNumber) {
        return getHeader()
                .get(BLOCK_BY_NUMBER_API_PATH.apply(blockNumber))
                .thenReturn();
    }

    public Response getBlockByHash(String blockHash) {
        return getHeader()
                .get(BLOCK_BY_HASH_API_PATH.apply(blockHash))
                .thenReturn();
    }

    public static Response postTransationsRequest(FilteredTransactionsRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(TRANSACTIONS_API_PATH)
                .thenReturn();
    }

    public Response getTransactionsByBlockNumber(BlockTransactionsByNumberRequest requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(TRANSACTIONS_BY_BLOCK_NUMBER_API_PATH)
                .thenReturn();
    }

    public Response getTransactionsByBlockHash(BlockTransactionsByHashRequest requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(TRANSACTIONS_BY_BLOCK_HASH_API_PATH)
                .thenReturn();
    }

    public Response postEventsRequest(FilteredEventsRequest requestObject) {
        return getHeader()
                .body(requestObject)
                .post(EVENTS_API_PATH)
                .thenReturn();
    }

    public Response getEventsByBlockNumber(BlockEventsByNumberRequest requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(EVENTS_BY_BLOCK_NUMBER_API_PATH)
                .thenReturn();
    }

    public Response getEventsByBlockHash(BlockEventsByHashRequest requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(EVENTS_BY_BLOCK_HASH_API_PATH)
                .thenReturn();
    }

    public Transaction getFirstAvailableTransaction() {
        val blockNumber = getFirstAvailableBlockWithTransactions().getBlockNumber();

        return getTransactionsByBlockNumber(BlockTransactionsByNumberRequest
                .requestBuilder()
                .blockNumber(blockNumber)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class)
                .getTransactions()[0];
    }

    public Event getFirstAvailableEvent() {
        return getFirstAvailableEvent(1);
    }

    public Event getFirstAvailableEvent(int numberOfEvents) {
        val blockNumber = getFirstAvailableBlockWithEvents(numberOfEvents).getBlockNumber();

        return getEventsByBlockNumber(BlockEventsByNumberRequest
                .requestBuilder()
                .blockNumber(blockNumber)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedEventsResponse.class)
                .getEvents()[0];
    }

    public Response getTransactionEvents(TransactionEventsRequest requestObject) {
        return getHeader()
                .queryParams(getQueryParams(requestObject))
                .get(TRANSACTION_EVENTS_API_PATH)
                .thenReturn();
    }

    public Response getTransactionDetailedInfoByTransactionHash(String transactionHash) {
        return getHeader()
                .get(TRANSACTION_DETAILS_BY_HASH_API_PATH.apply(transactionHash))
                .thenReturn();
    }
}
