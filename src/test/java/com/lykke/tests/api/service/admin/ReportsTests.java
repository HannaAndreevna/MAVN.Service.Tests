package com.lykke.tests.api.service.admin;

import static com.lykke.tests.api.common.prerequisites.Transfers.performTransfer;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;
import static com.lykke.tests.api.service.admin.ReportsUtils.getReports;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.api.testing.annotations.UserStoryId;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.service.admin.model.reports.ReportItemModel;
import com.lykke.tests.api.service.admin.model.reports.ReportListModel;
import com.lykke.tests.api.service.admin.model.reports.ReportRequestModel;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ReportsTests extends BaseApiTest {

    @Test
    @UserStoryId(3508)
    void shouldGetReport() {
        val transferData = performTransfer(1000.0, 100.0, 100.0);

        val expectedResult = ReportItemModel
                .builder()
                .amount("10") // hard-coded value
                .build();

        val actualResultCollection = getReports(ReportRequestModel
                .builder()
                .currentPage(1)
                .pageSize(500)
                .from(Instant.now().minus(30, ChronoUnit.DAYS).toString())
                .to(Instant.now().toString())
                .build(), getAdminToken())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ReportListModel.class)
                .getItems();

        // the last one as it's not possible to filter out results
        val actualResult = Arrays.stream(actualResultCollection)
                .skip(actualResultCollection.length - 1)
                .findFirst()
                .orElse(new ReportItemModel());

        assertEquals(expectedResult.getAmount(), actualResult.getAmount());
    }
}
