package com.lykke.tests.api.service.admin;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.params.provider.Arguments.of;

public class TestDataForPaginatedTests {

    //TODO("Integer.MAX_VALUE + 1 does not work. You will get "-2147483648" as result of this. We need to use Long")
    public static Stream<Arguments> getWrongPaginationParameters() {
        return Stream.of(
                of(0, 0),
                of(-1, -1),
                of(1, 0),
                of(0, 1),
                of(1, Integer.MAX_VALUE),
                of(Integer.MAX_VALUE, Integer.MAX_VALUE)
        );
    }

    public static Stream<Arguments> getInvalidCustomerIds() {
        return Stream.of(
                of(EMPTY),
                of("123123")
        );
    }
}
