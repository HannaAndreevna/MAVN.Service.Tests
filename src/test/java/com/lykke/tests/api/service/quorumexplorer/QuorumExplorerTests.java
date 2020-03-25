package com.lykke.tests.api.service.quorumexplorer;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.QuorumExplorer.InputValidation.BLOCK_BY_NUMBER_VALIDATION_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getBlockByHash;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getBlockByNumber;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getBlocks;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getEventsByBlockHash;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getEventsByBlockNumber;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getFirstAvailableBlock;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getFirstAvailableBlockWithEvents;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getFirstAvailableBlockWithTransactions;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getFirstAvailableEvent;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getFirstAvailableTransaction;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getTransactionDetailedInfoByTransactionHash;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getTransactionEvents;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getTransactionsByBlockHash;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.getTransactionsByBlockNumber;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.postEventsRequest;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.postTransationsRequest;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.DefectIds;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.PaginationErrorResponseModel;
import com.lykke.tests.api.service.quorumexplorer.model.Block;
import com.lykke.tests.api.service.quorumexplorer.model.BlockEventsByHashRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlockEventsByNumberRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlockResponse;
import com.lykke.tests.api.service.quorumexplorer.model.BlockTransactionsByHashRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlockTransactionsByNumberRequest;
import com.lykke.tests.api.service.quorumexplorer.model.BlocksErrorCode;
import com.lykke.tests.api.service.quorumexplorer.model.Event;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredEventsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredTransactionsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedBlocksResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedEventsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedTransactionsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.PaginationModel;
import com.lykke.tests.api.service.quorumexplorer.model.Transaction;
import com.lykke.tests.api.service.quorumexplorer.model.TransactionDetailsResponse;
import com.lykke.tests.api.service.quorumexplorer.model.TransactionEventsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.TransactionsErrorCode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class QuorumExplorerTests extends BaseApiTest {

    private static final int NUMBER_OF_EVENTS = 3;
    private static final String INVALID_STRING_VALUE = "aaa";
    private static final String SOME_ADDRESS = "aaa";
    private static final String SOME_BLOCK_HASH = "aaa";
    private static final String SOME_TRANSACTION_HASH = "aaa";
    private static final String THE_FIELD_BLOCK_NUMBER_MUST_BE_BETWEEN_0_AND_9_22337203685478_E_18_ERROR_MESSAGE = "The field blockNumber must be between 0 and 9.22337203685478E+18.";

    static Stream<Arguments> getInvalidPaginationParameters() {
        return Stream.of(
                of(INVALID_CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        PaginationErrorResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_LOWER_BOUNDARY)
                                .pageSize(PAGE_SIZE_UPPER_BOUNDARY).build()),
                of(INVALID_CURRENT_PAGE_UPPER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        PaginationErrorResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_UPPER_BOUNDARY)
                                .pageSize(PAGE_SIZE_UPPER_BOUNDARY).build()),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_LOWER_BOUNDARY,
                        PaginationErrorResponseModel.builder().currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                .pageSize(INVALID_PAGE_SIZE_LOWER_BOUNDARY).build()),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_UPPER_BOUNDARY,
                        PaginationErrorResponseModel.builder().currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                .pageSize(INVALID_PAGE_SIZE_UPPER_BOUNDARY).build())
        );
    }

    static Stream<Arguments> getInvalidLongValues() {
        return Stream.of(
                of(-1, 0),
                of(Long.MAX_VALUE, 1000)
        );
    }

    static Stream<Arguments> getInvalidStringValues() {
        return Stream.of(
                of(EMPTY, SC_BAD_REQUEST),
                of(INVALID_STRING_VALUE, SC_OK),
                of(GenerateUtils.generateRandomString(10), SC_OK),
                of(GenerateUtils.generateRandomString(100), SC_OK)
        );
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllBlocks() {
        val requestObject = new PaginationModel(CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY);
        val actualResult = getBlocks(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedBlocksResponse.class);

        assertAll(
                () -> assertNotEquals(0, actualResult.getBlocks().length),
                () -> assertNotNull(actualResult.getBlocks()[0].getBlockHash()),
                () -> assertNotNull(actualResult.getBlocks()[0].getBlockNumber()),
                () -> assertNotNull(actualResult.getBlocks()[0].getTimestamp()),
                () -> assertNotNull(actualResult.getBlocks()[0].getTransactionsCount()),
                () -> assertNotNull(actualResult.getBlocks()[0].getParentHash())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnAllBlocksOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val requestObject = new PaginationModel(currentPage, pageSize);
        val actualResult = getBlocks(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1627, 2604})
    void shouldReturnBlocksByNumber() {
        val expectedBlock = getFirstAvailableBlock();

        val actualBlockResponse = getBlockByNumber(expectedBlock.getBlockNumber())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockResponse.class);

        val actualBlock = actualBlockResponse.getBlock();

        assertAll(
                () -> assertEquals(BlocksErrorCode.NONE, actualBlockResponse.getError()),
                () -> assertEquals(expectedBlock.getBlockNumber(), actualBlock.getBlockNumber()),
                () -> assertEquals(expectedBlock.getBlockHash(), actualBlock.getBlockHash()),
                () -> assertEquals(expectedBlock.getParentHash(), actualBlock.getParentHash()),
                () -> assertEquals(expectedBlock.getTimestamp(), actualBlock.getTimestamp()),
                () -> assertEquals(expectedBlock.getTransactionsCount(), actualBlock.getTransactionsCount())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidLongValues")
    @UserStoryId(storyId = {1627, 2604})
    @DefectIds(2219)
    void shouldNotReturnBlocksByNumberOnInvalidInput(long invalidBlockNumber, long shift) {
        ValidationResponse expectedResult = ValidationResponse
                .builder()
                .modelErrors(ModelErrors
                        .builder()
                        .blockNumber(new String[]{
                                THE_FIELD_BLOCK_NUMBER_MUST_BE_BETWEEN_0_AND_9_22337203685478_E_18_ERROR_MESSAGE})
                        .build())
                .build();

        val actualResult = getHeader()
                .get(BLOCK_BY_NUMBER_VALIDATION_API_PATH.apply(invalidBlockNumber, shift))
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(storyId = {1627, 2604})
    void shouldReturnZeroBlockByNumber() {
        final long ZERO = 0L;
        val expectedResult = BlockResponse
                .builder()
                .block(Block
                        .builder()
                        .blockNumber(ZERO)
                        .timestamp(ZERO)
                        .transactionsCount(0)
                        .build())
                .error(BlocksErrorCode.NONE)
                .build();

        val actualResult = getHeader()
                .get(BLOCK_BY_NUMBER_VALIDATION_API_PATH.apply(ZERO, ZERO))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1627, 2604})
    void shouldReturnBlocksByHash() {
        val expectedBlock = getFirstAvailableBlock();

        val actualBlockResponse = getBlockByHash(expectedBlock.getBlockHash())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockResponse.class);

        val actualBlock = actualBlockResponse.getBlock();

        assertAll(
                () -> assertEquals(BlocksErrorCode.NONE, actualBlockResponse.getError()),
                () -> assertEquals(expectedBlock.getBlockNumber(), actualBlock.getBlockNumber()),
                () -> assertEquals(expectedBlock.getBlockHash(), actualBlock.getBlockHash()),
                () -> assertEquals(expectedBlock.getParentHash(), actualBlock.getParentHash()),
                () -> assertEquals(expectedBlock.getTimestamp(), actualBlock.getTimestamp()),
                () -> assertEquals(expectedBlock.getTransactionsCount(), actualBlock.getTransactionsCount())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(storyId = {1627, 2604})
    @DefectIds(2220)
    void shouldNotReturnBlocksByHashOnInvalidInput(String invalidHash, int status) {
        val statusHere = SC_BAD_REQUEST == status ? SC_NOT_FOUND : SC_OK;
        BlockResponse actualResult;
        val actualBlockResponse = getBlockByHash(invalidHash)
                .thenReturn();
        actualBlockResponse
                .then()
                .assertThat()
                .statusCode(statusHere);

        if (SC_NOT_FOUND != statusHere) {
            actualResult = actualBlockResponse
                    .then()
                    .assertThat()
                    .extract()
                    .as(BlockResponse.class);

            assertEquals(BlocksErrorCode.BLOCK_DOES_NOT_EXIST, actualResult.getError());
        }
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllTransactions() {
        val expectedTransaction = getFirstAvailableTransaction();

        val actualTransactions = postTransationsRequest(FilteredTransactionsRequest
                .builder()
                .from(new String[]{expectedTransaction.getFrom()})
                .to(new String[]{expectedTransaction.getTo()})
                .affectedAddresses(new String[]{})
                .pagingInfo(new PaginationModel(CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class)
                .getTransactions();

        val actualTransaction = Arrays.stream(actualTransactions)
                .filter(tran -> expectedTransaction.getBlockNumber() == tran.getBlockNumber())
                .findFirst()
                .orElse(new Transaction());

        assertAll(
                () -> assertEquals(expectedTransaction.getFrom(), actualTransaction.getFrom()),
                () -> assertEquals(expectedTransaction.getTo(), actualTransaction.getTo()),
                () -> assertEquals(expectedTransaction.getBlockHash(), actualTransaction.getBlockHash()),
                () -> assertEquals(expectedTransaction.getBlockNumber(), actualTransaction.getBlockNumber()),
                () -> assertEquals(expectedTransaction.getContractAddress(), actualTransaction.getContractAddress())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnAllTransactionsOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val actualResult = postTransationsRequest(FilteredTransactionsRequest
                .builder()
                .from(new String[]{})
                .to(new String[]{})
                .affectedAddresses(new String[]{})
                .pagingInfo(new PaginationModel(currentPage, pageSize))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllTransactionsByBlockNumber() {
        val blockNumber = getFirstAvailableBlockWithTransactions().getBlockNumber();

        val actualTransactions = getTransactionsByBlockNumber(BlockTransactionsByNumberRequest
                .requestBuilder()
                .blockNumber(blockNumber)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class);

        assertNotEquals(0, actualTransactions.getTotalCount());
    }

    @ParameterizedTest
    @MethodSource("getInvalidLongValues")
    @UserStoryId(1627)
    @DefectIds(2219)
    void shouldNotReturnAllTransactionsByBlockNumberOnInvalidInput(long invalidBlockNumber, long shift) {
        getTransactionsByBlockNumber(BlockTransactionsByNumberRequest
                .requestBuilder()
                .blockNumber(invalidBlockNumber + shift)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnAllTransactionsByBlockNumberOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val actualResult = getTransactionsByBlockNumber(BlockTransactionsByNumberRequest
                .requestBuilder()
                .blockNumber(1)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllTransactionsByBlockHash() {
        val blockHash = getFirstAvailableBlockWithTransactions().getBlockHash();

        val actualTransactions = getTransactionsByBlockHash(BlockTransactionsByHashRequest
                .requestBuilder()
                .blockHash(blockHash)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class);

        assertNotEquals(0, actualTransactions.getTotalCount());
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(1627)
    @DefectIds(2220)
    void shouldNotReturnAllTransactionsByBlockHashOnInvalidInput(String invalidHash, int status) {
        getTransactionsByBlockHash(BlockTransactionsByHashRequest
                .requestBuilder()
                .blockHash(invalidHash)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(status);
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllEvents() {
        val expectedEvent = getFirstAvailableEvent();

        val actualEvents = postEventsRequest(FilteredEventsRequest
                .builder()
                .address(expectedEvent.getAddress())
                .affectedAddresses(new String[]{})
                .eventName(expectedEvent.getEventName())
                .eventSignature(expectedEvent.getEventSignature())
                .pagingInfo(new PaginationModel(CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedEventsResponse.class)
                .getEvents();
        val actualEvent = Arrays.stream(actualEvents)
                .filter(event -> expectedEvent.getEventSignature().equals(event.getEventSignature())
                        && expectedEvent.getTransactionHash().equals(event.getTransactionHash())
                        && expectedEvent.getBlockNumber() == event.getBlockNumber())
                .findFirst()
                .orElse(new Event());

        assertAll(
                () -> assertEquals(expectedEvent.getAddress(), actualEvent.getAddress()),
                () -> assertEquals(expectedEvent.getBlockHash(), actualEvent.getBlockHash()),
                () -> assertEquals(expectedEvent.getBlockNumber(), actualEvent.getBlockNumber()),
                () -> assertEquals(expectedEvent.getEventName(), actualEvent.getEventName()),
                () -> assertEquals(expectedEvent.getEventSignature(), actualEvent.getEventSignature()),
                () -> assertEquals(expectedEvent.getTransactionHash(), actualEvent.getTransactionHash()),
                () -> assertEquals(expectedEvent.getLogIndex(), actualEvent.getLogIndex()),
                () -> assertEquals(expectedEvent.getTimestamp(), actualEvent.getTimestamp()),
                () -> assertEquals(expectedEvent.getTransactionIndex(), actualEvent.getTransactionIndex()),
                // the number of parameters returned is not predictable, for example there can 5 five of them:
                // operator, to, amount, data, operatorData
                () -> assertNotEquals(0, actualEvent.getParameters().length)
        );
    }

    @Disabled("TODO: this test can be used as a performance checker")
    @Test
    @UserStoryId(3230)
    void shouldReturnAllEventsForBlock() {
        val expectedEvent = getFirstAvailableEvent(NUMBER_OF_EVENTS);

        val actualEvents = postEventsRequest(FilteredEventsRequest
                .builder()
                .address(expectedEvent.getAddress())
                .affectedAddresses(new String[]{})
                .eventName(expectedEvent.getEventName())
                .eventSignature(expectedEvent.getEventSignature())
                .pagingInfo(new PaginationModel(CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedEventsResponse.class)
                .getEvents();
        val actualEvent = Arrays.stream(actualEvents)
                .filter(event -> expectedEvent.getEventSignature().equals(event.getEventSignature())
                        && expectedEvent.getTransactionHash().equals(event.getTransactionHash())
                        && expectedEvent.getBlockNumber() == event.getBlockNumber())
                .findFirst()
                .orElse(new Event());

        assertAll(
                () -> assertEquals(expectedEvent.getAddress(), actualEvent.getAddress()),
                () -> assertEquals(expectedEvent.getBlockHash(), actualEvent.getBlockHash()),
                () -> assertEquals(expectedEvent.getBlockNumber(), actualEvent.getBlockNumber()),
                () -> assertEquals(expectedEvent.getEventName(), actualEvent.getEventName()),
                () -> assertEquals(expectedEvent.getEventSignature(), actualEvent.getEventSignature()),
                () -> assertEquals(expectedEvent.getTransactionHash(), actualEvent.getTransactionHash()),
                () -> assertEquals(expectedEvent.getLogIndex(), actualEvent.getLogIndex()),
                () -> assertEquals(expectedEvent.getTimestamp(), actualEvent.getTimestamp()),
                () -> assertEquals(expectedEvent.getTransactionIndex(), actualEvent.getTransactionIndex()),
                // the number of parameters returned is not predictable, for example there can 5 five of them:
                // operator, to, amount, data, operatorData
                () -> assertNotEquals(0, actualEvent.getParameters().length)
        );
    }

    @Test
    @UserStoryId(3230)
    void shouldReturnAllEventsSortedByTimestampDesc() {
        val actualEvents = postEventsRequest(FilteredEventsRequest
                .builder()
                .affectedAddresses(new String[]{})
                .pagingInfo(new PaginationModel(CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedEventsResponse.class)
                .getEvents();

        val actualTimestamps = Arrays.stream(actualEvents)
                .collect(toList())
                .toArray(new Event[]{});
        val expectedSortedEvents1 = Arrays.stream(actualEvents)
                .collect(toList());

        Comparator<Event> comparator =
                (e1, e2) -> e1.getTimestamp() > e1.getTimestamp() ? 1 : 0;

        expectedSortedEvents1.sort(comparator.reversed());
        val expectedSortedEvents = expectedSortedEvents1
                .toArray(new Event[]{});

        assertArrayEquals(expectedSortedEvents, actualTimestamps);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnAllEventsOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val actualResult = postEventsRequest(FilteredEventsRequest
                .builder()
                .address(SOME_ADDRESS)
                .affectedAddresses(new String[]{})
                .pagingInfo(new PaginationModel(currentPage, pageSize))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllEventsByBlockNumber() {
        val blockNumber = getFirstAvailableBlockWithEvents().getBlockNumber();

        val actualEvents = getEventsByBlockNumber(BlockEventsByNumberRequest
                .requestBuilder()
                .blockNumber(blockNumber)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class);

        assertNotEquals(0, actualEvents.getTotalCount());
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnAllEventsByBlockNumberOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val blockNumber = 1;

        val actualResult = getEventsByBlockNumber(BlockEventsByNumberRequest
                .requestBuilder()
                .blockNumber(blockNumber)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnAllEventsByBlockHash() {
        val blockHash = getFirstAvailableBlockWithEvents().getBlockHash();

        val actualEvents = getEventsByBlockHash(BlockEventsByHashRequest
                .requestBuilder()
                .blockHash(blockHash)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class);

        assertNotEquals(0, actualEvents.getTotalCount());
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(1627)
    @DefectIds(2220)
    void shouldNotReturnAllEventsByBlockHashOnInvalidInput(String invalidHash, int status) {
        getEventsByBlockHash(BlockEventsByHashRequest
                .requestBuilder()
                .blockHash(invalidHash)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnAllEventsByBlockHashOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val blockHash = SOME_BLOCK_HASH;

        val actualResult = getEventsByBlockHash(BlockEventsByHashRequest
                .requestBuilder()
                .blockHash(blockHash)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(1627)
    void shouldReturnTransactionEvents() {
        val blockWithEvents = getFirstAvailableBlockWithEvents();
        val transactionHash = getTransactionsByBlockNumber(
                BlockTransactionsByNumberRequest.requestBuilder().blockNumber(blockWithEvents.getBlockNumber())
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY).pageSize(PAGE_SIZE_UPPER_BOUNDARY).build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedTransactionsResponse.class)
                .getTransactions()[0]
                .getTransactionHash();

        val actualEvents = getTransactionEvents(TransactionEventsRequest
                .requestBuilder()
                .transactionHash(transactionHash)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedEventsResponse.class);

        assertNotEquals(0, actualEvents.getEvents().length);
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(1627)
    @DefectIds(2220)
    void shouldNotReturnTransactionEventsOnInvalidInput(String invalidHash, int status) {
        getTransactionEvents(TransactionEventsRequest
                .requestBuilder()
                .transactionHash(invalidHash)
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(status);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(1627)
    @DefectIds(2208)
    void shouldNotReturnTransactionEventsOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val transactionHash = SOME_TRANSACTION_HASH;

        val actualResult = getTransactionEvents(TransactionEventsRequest
                .requestBuilder()
                .transactionHash(transactionHash)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(storyId = {1627, 2604})
    void shouldReturnDetailedTransactionInfoByTransactionHash() {
        val transactionHash = getFirstAvailableTransaction().getTransactionHash();

        val actualTransactionDetailResponse = getTransactionDetailedInfoByTransactionHash(transactionHash)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionDetailsResponse.class);

        val actualTransactionInfo = actualTransactionDetailResponse.getTransaction();

        assertAll(
                () -> assertEquals(TransactionsErrorCode.NONE, actualTransactionDetailResponse.getError()),
                () -> assertEquals(transactionHash, actualTransactionInfo.getTransactionHash())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(storyId = {1627, 2604})
    @DefectIds(2220)
    void shouldNotReturnDetailedTransactionInfoByTransactionHashOnInvalidInput(String invalidHash, int status) {
        val statusHere = SC_BAD_REQUEST == status ? SC_NOT_FOUND : SC_OK;
        TransactionDetailsResponse actualResult;
        val actualTransactionDetailResponse = getTransactionDetailedInfoByTransactionHash(invalidHash)
                .thenReturn();
        actualTransactionDetailResponse
                .then()
                .assertThat()
                .statusCode(statusHere);

        if (SC_NOT_FOUND != statusHere) {
            actualResult = actualTransactionDetailResponse
                    .then()
                    .assertThat()
                    .extract()
                    .as(TransactionDetailsResponse.class);

            assertEquals(TransactionsErrorCode.TRANSACTION_DOES_NOT_EXIST, actualResult.getError());
        }
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationResponse {

        private String errorMessage;
        private ModelErrors modelErrors;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    public static class ModelErrors {

        @JsonProperty("blockNumber")
        private String[] blockNumber;
    }
}
