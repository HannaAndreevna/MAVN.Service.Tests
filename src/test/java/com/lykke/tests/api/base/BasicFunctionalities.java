package com.lykke.tests.api.base;

import static com.lykke.api.testing.api.common.PasswordGen.generateInvalidPassword;
import static com.lykke.tests.api.common.CommonConsts.NON_LATIN_PWD;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.lykke.tests.api.service.admin.model.customerhistory.CustomerOperationTransactionType;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.junit.jupiter.params.provider.Arguments;

public class BasicFunctionalities {

    public static final String TEST_ASSET = "TestAsset";
    public static final CustomerOperationTransactionType P2P_TRANSFER = CustomerOperationTransactionType.P_2_P;
    public static final CustomerOperationTransactionType EARN_TRANSACTION = CustomerOperationTransactionType.EARN;
    public static final String BASE_ASSET = "MVN";
    public static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DATE_FORMAT_M_D_YYYY = new SimpleDateFormat("M/d/yyyy");
    public static final SimpleDateFormat TIME_FORMAT_HH_MM_SS = new SimpleDateFormat("HH:mm:ss");

    public static Calendar getCurrentDate() {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        return date;
    }

    public static Calendar getTomorrowsDate() {
        val date = Date.from(Instant.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        val calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String getYesterdayDateString() {
        return DATE_FORMAT_YYYY_MM_DD.format(yesterday());
    }

    public static String getTodayDateString() {
        return DATE_FORMAT_M_D_YYYY.format(today());
    }

    public static String getTomorrowDateString() {
        return DATE_FORMAT_M_D_YYYY.format(tomorrow());
    }

    private static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private static Date today() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        return cal.getTime();
    }

    private static Date tomorrow() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Stream<Arguments> getInvalidPasswords() {
        return Stream.of(
                of(EMPTY),
                of(generateInvalidPassword(1, 3, true, true, true, true)),
                of(generateInvalidPassword(60, 100, true, true, true, true)),
                of(generateInvalidPassword(8, 50, true, true, true, false)),
                of(generateInvalidPassword(8, 50, true, true, false, true)),
                of(generateInvalidPassword(8, 50, true, false, true, true)),
                of(generateInvalidPassword(8, 50, false, true, true, true)),
                of(NON_LATIN_PWD)
        );
    }
}
