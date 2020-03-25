package com.lykke.tests.api.common;

public class PaginationConts {

    public static final int CURRENT_PAGE_LOWER_BOUNDARY = 1;
    public static final int CURRENT_PAGE_UPPER_BOUNDARY = 10000;
    public static final int PAGE_SIZE_LOWER_BOUNDARY = 1;
    public static final int PAGE_SIZE_UPPER_BOUNDARY = 500;

    public static final int INVALID_CURRENT_PAGE_LOWER_BOUNDARY = CURRENT_PAGE_LOWER_BOUNDARY - 1;
    public static final int INVALID_CURRENT_PAGE_UPPER_BOUNDARY = CURRENT_PAGE_UPPER_BOUNDARY + 1;
    public static final int INVALID_PAGE_SIZE_LOWER_BOUNDARY = PAGE_SIZE_LOWER_BOUNDARY - 1;
    public static final int INVALID_PAGE_SIZE_UPPER_BOUNDARY = PAGE_SIZE_UPPER_BOUNDARY + 1;

    public static final String PAGE_SIZE_ERROR_MESSAGE = "The field PageSize must be between 1 and 500.";
    public static final String CURRENT_PAGE_ERROR_MESSAGE = "The field CurrentPage must be between 1 and 10000.";
}
