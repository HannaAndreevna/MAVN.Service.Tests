package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.EVENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.InputValidation.BLOCK_BY_NUMBER_VALIDATION_API_PATH;
import static com.lykke.tests.api.common.CommonConsts.Currency.TOKEN_TO_ATTO_RATE;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static com.lykke.tests.api.common.PaginationConts.CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_CURRENT_PAGE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_LOWER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.INVALID_PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.common.PaginationConts.PAGE_SIZE_UPPER_BOUNDARY;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getBlockByHash;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getBlockByNumber;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getBlocks;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getEvents;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getEventsByBlockHash;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getEventsByBlockNumber;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getFirstAvailableBlock;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getFirstAvailableBlockWithEvents;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getFirstAvailableBlockWithTransactions;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getFirstAvailableEvent;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getFirstAvailableTransaction;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getTransactionDetailedInfoByTransactionHash;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getTransactionEvents;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getTransactionsByBlockHash;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getTransactionsByBlockNumber;
import static com.lykke.tests.api.service.admin.BlockchainUtils.getTransations;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils.postEventsRequest;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.common.model.PaginationErrorResponseModel;
import com.lykke.tests.api.service.admin.model.blockchain.BlockListRequest;
import com.lykke.tests.api.service.admin.model.blockchain.BlockListResponse;
import com.lykke.tests.api.service.admin.model.blockchain.BlockModel;
import com.lykke.tests.api.service.admin.model.blockchain.EventListRequest;
import com.lykke.tests.api.service.admin.model.blockchain.EventListResponse;
import com.lykke.tests.api.service.admin.model.blockchain.EventModel;
import com.lykke.tests.api.service.admin.model.blockchain.EventParameters;
import com.lykke.tests.api.service.admin.model.blockchain.PagedRequestModel;
import com.lykke.tests.api.service.admin.model.blockchain.PagedResponseModel;
import com.lykke.tests.api.service.admin.model.blockchain.PaginationModel;
import com.lykke.tests.api.service.admin.model.blockchain.TransactionListRequest;
import com.lykke.tests.api.service.admin.model.blockchain.TransactionListResponse;
import com.lykke.tests.api.service.admin.model.blockchain.TransactionModel;
import com.lykke.tests.api.service.quorumexplorer.QuorumExplorerUtils;
import com.lykke.tests.api.service.quorumexplorer.model.Event;
import com.lykke.tests.api.service.quorumexplorer.model.EventParameter;
import com.lykke.tests.api.service.quorumexplorer.model.FilteredEventsRequest;
import com.lykke.tests.api.service.quorumexplorer.model.PaginatedEventsResponse;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BlockchainTests extends BaseApiTest {

    private static final String AMOUNT = "amount";
    private static final String TECHNICAL_PROBLEM_ERROR_MESSAGE = "Technical problem";
    private static final String INVALID_STRING_VALUE = "aaa";
    private static final String SOME_ADDRESS = "aaa";
    private static final String SOME_BLOCK_HASH = "aaa";
    private static final String SOME_TRANSACTION_HASH = "aaa";
    private static final String THE_PAGED_REQUEST_FIELD_IS_REQUIRED_ERROR_MESSAGE = "The PagedRequest field is required.";

    static Stream<Arguments> getInvalidPaginationParameters() {
        return Stream.of(
                of(INVALID_CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        PaginationErrorResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_LOWER_BOUNDARY).build()),
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

    static Stream<Arguments> getInvalidPaginationParameters1111() {
        return Stream.of(
                of(INVALID_CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        BlockListResponse.builder().pagedResponse(PagedResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_LOWER_BOUNDARY).build()).build()),
                of(INVALID_CURRENT_PAGE_UPPER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        BlockListResponse.builder().pagedResponse(PagedResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_UPPER_BOUNDARY)
                                .build()).build()),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_LOWER_BOUNDARY,
                        BlockListResponse.builder()
                                .pagedResponse(PagedResponseModel.builder().currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                        .build()).build()),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_UPPER_BOUNDARY,
                        BlockListResponse.builder()
                                .pagedResponse(PagedResponseModel.builder().currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                        .build()).build())
        );
    }

    static Stream<Arguments> getInvalidPaginationParametersForEvents() {
        return Stream.of(
                of(INVALID_CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        EventListResponse.builder().pagedResponse(PagedResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_LOWER_BOUNDARY).build()).build()),
                of(INVALID_CURRENT_PAGE_UPPER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY,
                        EventListResponse.builder().pagedResponse(PagedResponseModel.builder().currentPage(
                                INVALID_CURRENT_PAGE_UPPER_BOUNDARY)
                                .build()).build()),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_LOWER_BOUNDARY,
                        EventListResponse.builder()
                                .pagedResponse(PagedResponseModel.builder().currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                        .build()).build()),
                of(CURRENT_PAGE_LOWER_BOUNDARY, INVALID_PAGE_SIZE_UPPER_BOUNDARY,
                        EventListResponse.builder()
                                .pagedResponse(PagedResponseModel.builder()
                                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                                        .build()).build())
        );
    }

    static Stream<Arguments> getInvalidLongValues() {
        return Stream.of(
                of(-1, 0, SC_INTERNAL_SERVER_ERROR),
                of(0, 0, SC_OK),
                of(Long.MAX_VALUE, 1000, SC_INTERNAL_SERVER_ERROR)
        );
    }

    static Stream<Arguments> getInvalidLongValues1() {
        return Stream.of(
                of(0, 0, SC_OK, BlockModel.builder().build())
        );
    }

    static Stream<Arguments> getInvalidLongValues2() {
        return Stream.of(
                of(-1, 0, SC_INTERNAL_SERVER_ERROR, ValidationErrorResponse.builder().message(
                        TECHNICAL_PROBLEM_ERROR_MESSAGE).build()),
                of(Long.MAX_VALUE, 1000, SC_INTERNAL_SERVER_ERROR, ValidationErrorResponse.builder().message(
                        TECHNICAL_PROBLEM_ERROR_MESSAGE).build())
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
    @UserStoryId(2611)
    void shouldReturnAllBlocks() {
        val requestObject = new BlockListRequest(PAGE_SIZE_UPPER_BOUNDARY, CURRENT_PAGE_LOWER_BOUNDARY);
        val actualResult = getBlocks(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockListResponse.class);

        val actualFirstBlock = actualResult.getBlocks()[0];
        assertAll(
                () -> assertNotEquals(0, actualResult.getBlocks().length),
                () -> assertNotNull(actualFirstBlock.getBlockHash()),
                () -> assertNotNull(actualFirstBlock.getBlockNumber()),
                () -> assertNotNull(actualFirstBlock.getTimestamp()),
                () -> assertNotNull(actualFirstBlock.getTransactionsCount()),
                () -> assertNotNull(actualFirstBlock.getParentHash())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters1111")
    @UserStoryId(2611)
    void shouldNotReturnAllBlocksOnInvalidInput(int currentPage, int pageSize,
            BlockListResponse expectedResult) {
        val requestObject = new BlockListRequest(pageSize, currentPage);
        val actualResult = getBlocks(requestObject)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(BlockListResponse.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2611)
    void shouldReturnBlocksByNumber() {
        val expectedBlock = getFirstAvailableBlock();

        val actualBlock = getBlockByNumber(expectedBlock.getBlockNumber())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockModel.class);

        assertAll(
                () -> assertEquals(expectedBlock.getBlockNumber(), actualBlock.getBlockNumber()),
                () -> assertEquals(expectedBlock.getBlockHash(), actualBlock.getBlockHash()),
                () -> assertEquals(expectedBlock.getParentHash(), actualBlock.getParentHash()),
                () -> assertEquals(expectedBlock.getTimestamp(), actualBlock.getTimestamp()),
                () -> assertEquals(expectedBlock.getTransactionsCount(), actualBlock.getTransactionsCount())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidLongValues1")
    @UserStoryId(2611)
    void shouldReturnEmptyBlocksByNumberOnInvalidInput(long invalidBlockNumber, long shift, int status,
            BlockModel expectedBlock) {
        val actualBlock = getHeader(getAdminToken())
                .get(BLOCK_BY_NUMBER_VALIDATION_API_PATH.apply(invalidBlockNumber, shift))
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(BlockModel.class);

        assertAll(
                () -> assertEquals(expectedBlock.getBlockNumber(), actualBlock.getBlockNumber()),
                () -> assertEquals(expectedBlock.getParentHash(), actualBlock.getParentHash()),
                () -> assertEquals(expectedBlock.getTransactionsCount(), actualBlock.getTransactionsCount())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidLongValues2")
    @UserStoryId(2611)
    void shouldNotReturnBlocksByNumberOnInvalidInput(long invalidBlockNumber, long shift, int status,
            ValidationErrorResponse expectedResult) {
        val actualResult = getHeader(getAdminToken())
                .get(BLOCK_BY_NUMBER_VALIDATION_API_PATH.apply(invalidBlockNumber, shift))
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(ValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @Tag(SMOKE_TEST)
    @UserStoryId(2611)
    void shouldReturnBlocksByHash() {
        val expectedBlock = getFirstAvailableBlock();

        val actualBlock = getBlockByHash(expectedBlock.getBlockHash())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(BlockModel.class);

        assertAll(
                () -> assertEquals(expectedBlock.getBlockNumber(), actualBlock.getBlockNumber()),
                () -> assertEquals(expectedBlock.getBlockHash(), actualBlock.getBlockHash()),
                () -> assertEquals(expectedBlock.getParentHash(), actualBlock.getParentHash()),
                () -> assertEquals(expectedBlock.getTimestamp(), actualBlock.getTimestamp()),
                () -> assertEquals(expectedBlock.getTransactionsCount(), actualBlock.getTransactionsCount())
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(2611)
    void shouldNotReturnBlocksByHashOnInvalidInput(String invalidHash) {
        val expectedStatus = EMPTY.equalsIgnoreCase(invalidHash) ? SC_NOT_FOUND : SC_OK;
        getBlockByHash(invalidHash)
                .then()
                .assertThat()
                .statusCode(expectedStatus);
    }

    @Test
    @UserStoryId(2611)
    void shouldReturnAllTransactions() {
        val expectedTransaction = getFirstAvailableTransaction();

        val actualTransactions = getTransations(TransactionListRequest
                .builder()
                .from(new String[]{expectedTransaction.getFrom()})
                .to(new String[]{expectedTransaction.getTo()})
                .affectedAddresses(new String[]{})
                .pagedRequest(new PagedRequestModel(PAGE_SIZE_UPPER_BOUNDARY, CURRENT_PAGE_LOWER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionListResponse.class)
                .getTransactions();

        val actualTransaction = Arrays.stream(actualTransactions)
                .filter(tran -> expectedTransaction.getBlockNumber() == tran.getBlockNumber())
                .findFirst()
                .orElse(new TransactionModel());

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
    @UserStoryId(2611)
    void shouldNotReturnAllTransactionsOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val actualResult = getTransations(TransactionListRequest
                .builder()
                .from(new String[]{})
                .to(new String[]{})
                .affectedAddresses(new String[]{})
                .pagedRequest(new PagedRequestModel(pageSize, currentPage))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(PaginationErrorResponseModel.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2611)
    void shouldReturnAllTransactionsByBlockNumber() {
        val blockNumber = getFirstAvailableBlockWithTransactions().getBlockNumber();

        val actualTransactions = getTransactionsByBlockNumber(blockNumber, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionListResponse.class);

        assertNotEquals(0, actualTransactions.getTransactions().length);
    }

    @ParameterizedTest
    @MethodSource("getInvalidLongValues")
    @UserStoryId(2611)
    void shouldNotReturnAllTransactionsByBlockNumberOnInvalidInput(long invalidBlockNumber, long shift, int status) {
        getTransactionsByBlockNumber(invalidBlockNumber + shift, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .as(PaginationErrorResponseModel.class);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(2611)
    void shouldNotReturnAllTransactionsByBlockNumberOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val actualResult = getTransactionsByBlockNumber(1, PaginationModel
                .builder()
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
    @UserStoryId(2611)
    void shouldReturnAllTransactionsByBlockHash() {
        val blockHash = getFirstAvailableBlockWithTransactions().getBlockHash();

        val actualTransactions = getTransactionsByBlockHash(blockHash, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionListResponse.class);

        assertNotEquals(0, actualTransactions.getTransactions().length);
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(2611)
    void shouldNotReturnAllTransactionsByBlockHashOnInvalidInput(String invalidHash, int status) {
        // TODO: now it's not possible to get admin token in the first case
        if (SC_BAD_REQUEST == status) {
            return;
        }

        val statusHere = SC_BAD_REQUEST == status ? SC_NOT_FOUND : SC_OK;
        getTransactionsByBlockHash(invalidHash, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(statusHere);
    }

    @Test
    @UserStoryId(2611)
    void shouldReturnAllEvents() {
        val expectedEvent = getFirstAvailableEvent();
        val actualEvents = getEvents(EventListRequest
                .builder()
                .address(expectedEvent.getAddress())
                .affectedAddresses(new String[]{})
                .eventName(expectedEvent.getEventName())
                .eventSignature(expectedEvent.getEventSignature())
                .pagedRequest(new PagedRequestModel(PAGE_SIZE_UPPER_BOUNDARY, CURRENT_PAGE_LOWER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EventListResponse.class)
                .getEvents();

        assertTrue(0 < actualEvents.length);
    }

    @Test
    @UserStoryId(3214)
    void shouldConvertAmountFromAtto() {
        val quorumExplorerEvent = QuorumExplorerUtils.getFirstAvailableEvent();
        val quorumExplorerEvents = postEventsRequest(FilteredEventsRequest
                .builder()
                .address(quorumExplorerEvent.getAddress())
                .affectedAddresses(new String[]{})
                .eventName(quorumExplorerEvent.getEventName())
                .eventSignature(quorumExplorerEvent.getEventSignature())
                .pagingInfo(new com.lykke.tests.api.service.quorumexplorer.model.PaginationModel(
                        CURRENT_PAGE_LOWER_BOUNDARY, PAGE_SIZE_UPPER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(PaginatedEventsResponse.class)
                .getEvents();
        val actualQuorumExplorerEvent = Arrays.stream(quorumExplorerEvents)
                .filter(event -> quorumExplorerEvent.getEventSignature().equals(event.getEventSignature())
                        && quorumExplorerEvent.getTransactionHash().equals(event.getTransactionHash())
                        && quorumExplorerEvent.getBlockNumber() == event.getBlockNumber())
                .findFirst()
                .orElse(new Event());

        val expectedAmount = Long.valueOf(Arrays.stream(actualQuorumExplorerEvent.getParameters())
                .filter(param -> AMOUNT.equalsIgnoreCase(param.getName()))
                .findFirst()
                .orElse(new EventParameter())
                .getValue()) / TOKEN_TO_ATTO_RATE;
        val actualEvents = getEvents(EventListRequest
                .builder()
                .address(quorumExplorerEvent.getAddress())
                .affectedAddresses(new String[]{})
                .eventName(quorumExplorerEvent.getEventName())
                .eventSignature(quorumExplorerEvent.getEventSignature())
                .pagedRequest(new PagedRequestModel(PAGE_SIZE_UPPER_BOUNDARY, CURRENT_PAGE_LOWER_BOUNDARY))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EventListResponse.class)
                .getEvents();
        val actualEvent = Arrays.stream(actualEvents)
                .findFirst()
                .orElse(new EventModel());
        val actualAmount = Arrays.stream(actualEvent.getParameters())
                .filter(param -> AMOUNT.equalsIgnoreCase(param.getName()))
                .findFirst()
                .orElse(new EventParameters())
                .getValue();

        assertEquals(expectedAmount, Long.valueOf(actualAmount));
    }

    @Test
    @UserStoryId(2704)
    void shouldNotFailReturningAllEventskOnInvalidQueryParameters() {
        // wrong query parameter: PagedRequest=PagedRequestModel(pageSize=500, currentPage=1)
        val expectedResult = QueryParametersValidationErrorResponse
                .builder()
                .pagedRequest(new String[]{THE_PAGED_REQUEST_FIELD_IS_REQUIRED_ERROR_MESSAGE})
                .build();
        val actualResult = getHeader(getAdminToken())
                .queryParams("PagedRequest", "PagedRequestModel(pageSize=500, currentPage=1)")
                .get(EVENTS_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(QueryParametersValidationErrorResponse.class);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(2611)
    void shouldNotReturnAllEventsOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val actualResult = getEvents(EventListRequest
                .builder()
                .address(SOME_ADDRESS)
                .affectedAddresses(new String[]{})
                .pagedRequest(new PagedRequestModel(pageSize, currentPage))
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(EventListResponse.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2611)
    void shouldReturnAllEventsByBlockNumber() {
        val blockNumber = getFirstAvailableBlockWithEvents().getBlockNumber();

        EventListResponse actualEvents = getEventsByBlockNumber(blockNumber, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EventListResponse.class);

        assertNotEquals(0, actualEvents.getEvents().length);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParametersForEvents")
    @UserStoryId(2611)
    void shouldNotReturnAllEventsByBlockNumberOnInvalidInput(int currentPage, int pageSize,
            EventListResponse expectedResult) {
        val blockNumber = 1;

        EventListResponse actualResult = getEventsByBlockNumber(blockNumber, PaginationModel
                .builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(EventListResponse.class);

        assertNotEquals(expectedResult, actualResult);
    }

    @Test
    @UserStoryId(2611)
    void shouldReturnAllEventsByBlockHash() {
        val blockHash = getFirstAvailableBlockWithEvents().getBlockHash();

        val actualEvents = getEventsByBlockHash(blockHash, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EventListResponse.class);

        assertNotEquals(0, actualEvents.getEvents().length);
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(2611)
    void shouldNotReturnAllEventsByBlockHashOnInvalidInput(String invalidHash) {
        val expectedStatus = EMPTY.equalsIgnoreCase(invalidHash) ? SC_NOT_FOUND : SC_OK;

        getEventsByBlockHash(invalidHash, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(expectedStatus);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(2611)
    void shouldNotReturnAllEventsByBlockHashOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val blockHash = SOME_BLOCK_HASH;

        val actualResult = getEventsByBlockHash(blockHash, PaginationModel
                .builder()
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
    @UserStoryId(2611)
    void shouldReturnTransactionEvents() {
        val blockWithEvents = getFirstAvailableBlockWithEvents();
        val transactionHash = getTransactionsByBlockNumber(
                blockWithEvents.getBlockNumber(),
                PaginationModel.builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY).pageSize(PAGE_SIZE_UPPER_BOUNDARY).build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionListResponse.class)
                .getTransactions()[0]
                .getTransactionHash();

        val actualEvents = getTransactionEvents(transactionHash, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(EventListResponse.class);

        assertNotEquals(0, actualEvents.getEvents().length);
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(2611)
    void shouldNotReturnTransactionEventsOnInvalidInput(String invalidHash) {
        val expectedStatus = EMPTY.equalsIgnoreCase(invalidHash) ? SC_NOT_FOUND : SC_OK;

        val expectedResult = EventListResponse
                .builder()
                .pagedResponse(PagedResponseModel
                        .builder()
                        .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                        .build())
                .events(new EventModel[]{})
                .build();

        val actualResponse = getTransactionEvents(invalidHash, PaginationModel
                .builder()
                .currentPage(CURRENT_PAGE_LOWER_BOUNDARY)
                .pageSize(PAGE_SIZE_UPPER_BOUNDARY)
                .build())
                .thenReturn();

        actualResponse
                .then()
                .assertThat()
                .statusCode(expectedStatus);
        if (SC_OK == expectedStatus) {
            val actualResult = actualResponse
                    .then()
                    .extract()
                    .as(EventListResponse.class);

            assertEquals(expectedResult, actualResult);
        }
    }

    @ParameterizedTest
    @MethodSource("getInvalidPaginationParameters")
    @UserStoryId(2611)
    void shouldNotReturnTransactionEventsOnInvalidInput(int currentPage, int pageSize,
            PaginationErrorResponseModel expectedResult) {
        val transactionHash = SOME_TRANSACTION_HASH;

        val actualResult = getTransactionEvents(transactionHash, PaginationModel
                .builder()
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
    @UserStoryId(2611)
    void shouldReturnDetailedTransactionInfoByTransactionHash() {
        val transactionHash = getFirstAvailableTransaction().getTransactionHash();

        val actualTransactionInfo = getTransactionDetailedInfoByTransactionHash(transactionHash)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(TransactionModel.class);

        assertEquals(transactionHash, actualTransactionInfo.getTransactionHash());
    }

    @ParameterizedTest
    @MethodSource("getInvalidStringValues")
    @UserStoryId(2611)
    void shouldNotReturnDetailedTransactionInfoByTransactionHashOnInvalidInput(String invalidHash) {
        val expectedStatus = EMPTY.equalsIgnoreCase(invalidHash) ? SC_BAD_REQUEST : SC_OK;
        getTransactionDetailedInfoByTransactionHash(invalidHash)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .as(TransactionModel.class);
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(LowerCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ValidationErrorResponse {

        private String message;
    }

    @AllArgsConstructor
    @Builder
    @Data
    @NoArgsConstructor
    @JsonNaming(UpperCamelCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueryParametersValidationErrorResponse {

        private String[] pagedRequest;
    }
}
