package com.lykke.tests.api.service.tokenstatisticsjob;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static com.lykke.tests.api.base.Paths.TokenStatistics.TOKEN_STATISTICS_BY_DAYS_API_PATH;
import static com.lykke.tests.api.base.Paths.TokenStatistics.TOKEN_STATISTICS_JOB_GENERAL_TOKENS_BY_DATA_PATH;
import static com.lykke.tests.api.base.Paths.TokenStatistics.TOKEN_STATISTICS_JOB_GENERAL_TOKENS_CURRENT_PATH;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.tokenstatisticsjob.model.general.ResponseModel;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokenStatisticsRequest;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokensRequest;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.TokensStatisticListResponse;
import com.lykke.tests.api.service.tokenstatisticsjob.model.general.ValidationErrorResponseModel;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenStatisticsJobUtils {

    public static final String DATE_FIELD = "dt";

    public ResponseModel getTokensByDate(TokensRequest requestObject) {
        return getHeader()
                .queryParams(getTokensByDateObject(requestObject))
                .get(TOKEN_STATISTICS_JOB_GENERAL_TOKENS_BY_DATA_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ResponseModel.class);

    }

    public ResponseModel getTokenCurrent() {
        return getHeader()
                .get(TOKEN_STATISTICS_JOB_GENERAL_TOKENS_CURRENT_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(ResponseModel.class);
    }

    public ValidationErrorResponseModel getValidationErrorResponseModel(TokensRequest requestObject) {
        return getHeader()
                .queryParams(getTokensByDateObject(requestObject))
                .get(TOKEN_STATISTICS_JOB_GENERAL_TOKENS_BY_DATA_PATH)
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ValidationErrorResponseModel.class);
    }

    public TokensStatisticListResponse getTokensStatisticsForPeriodByDays(
            TokenStatisticsRequest tokensRequest) {
        return getHeader()
                .queryParams(getQueryParams(tokensRequest))
                .get(TOKEN_STATISTICS_BY_DAYS_API_PATH)
                .then()
                .assertThat()
                .statusCode(tokensRequest.getHttpStatus())
                .extract()
                .as(TokensStatisticListResponse.class);
    }

    public TokensStatisticListResponse getTokensListValidationResponse(
            TokenStatisticsRequest tokensRequest) {
        return getHeader()
                .queryParams(getQueryParams(tokensRequest))
                .get(TOKEN_STATISTICS_BY_DAYS_API_PATH)
                .then()
                .assertThat()
                .statusCode(tokensRequest.getHttpStatus())
                .extract()
                .as(TokensStatisticListResponse.class);
    }

    private Map<String, String> getTokensByDateObject(TokensRequest requestParameters) {
        return Stream.of(new String[][]{
                {DATE_FIELD, requestParameters.getDate()}
        })
                .collect(toMap(item -> item[0], item -> null == item[1] ? EMPTY : item[1]));
    }
}
