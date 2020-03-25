package com.lykke.tests.api.common;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.api.testing.annotations.NetClassName;
import com.lykke.api.testing.annotations.QueryParameters;
import com.lykke.tests.api.base.Paths;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class HelperUtils {

    public static void simulateTrigger(SimulateTriggerRequest requestModel, Map<String, String> data) {
        getHeader()
                .queryParams(getQueryParams(requestModel, true))
                .body(data)
                .post(Paths.BONUS_ENGINE_SIMULATE_TRIGGER_API_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    public static String getImagePath(String imageName) {
        return System.getProperty("user.dir") + "/src/main/resources/assets/" + imageName;
    }

    public static String getTestFilePath(String fileName) {
        return System.getProperty("user.dir") + "/src/main/resources/assets/testfiles/" + fileName;
    }

    @SneakyThrows
    public static void createCsvFile(String fileName, String code) {
        val filePath = getImagePath(fileName);
        val file = new File(filePath);
        try (PrintWriter pw = new PrintWriter(filePath)) {
            // TODO: refactor thiis
            pw.println(code);
        }
    }

    @AllArgsConstructor
    @Builder
    @Data
    @QueryParameters
    @NetClassName("none")
    public static class SimulateTriggerRequest {

        private String customerId;
        private String partnerId;
        private String conditionType;
    }
}
