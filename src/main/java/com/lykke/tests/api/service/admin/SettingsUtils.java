package com.lykke.tests.api.service.admin;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.Paths.AdminApi.SETTINGS_AGENT_REQUIREMENTS_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.SETTINGS_GLOBAL_CURRENCY_RATE_API_PATH;
import static com.lykke.tests.api.base.Paths.AdminApi.SETTINGS_OPERATION_FEES_API_PATH;
import static com.lykke.tests.api.service.admin.LoginAdminUtils.getAdminToken;

import com.lykke.tests.api.base.Paths.AdminApi;
import com.lykke.tests.api.service.admin.model.settings.AgentRequirementUpdateRequest;
import com.lykke.tests.api.service.admin.model.settings.GlobalCurrencyRateModel;
import com.lykke.tests.api.service.admin.model.settings.OperationFeesModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SettingsUtils {

    Response getGlobalCurrencyRate() {
        return getHeader(getAdminToken())
                .get(SETTINGS_GLOBAL_CURRENCY_RATE_API_PATH)
                .thenReturn();
    }

    Response updateGlobalCurrencyRate(GlobalCurrencyRateModel requestModel) {
        return getHeader(getAdminToken())
                .body(requestModel)
                .put(SETTINGS_GLOBAL_CURRENCY_RATE_API_PATH)
                .thenReturn();
    }

    Response getAgentRequirements() {
        return getHeader(getAdminToken())
                .get(SETTINGS_AGENT_REQUIREMENTS_API_PATH)
                .thenReturn();
    }

    Response updateAgentRequirements(AgentRequirementUpdateRequest requestModel) {
        return getHeader(getAdminToken())
                .body(requestModel)
                .put(SETTINGS_AGENT_REQUIREMENTS_API_PATH)
                .thenReturn();
    }

    Response getOperationFees() {
        return getHeader(getAdminToken())
                .get(SETTINGS_OPERATION_FEES_API_PATH)
                .thenReturn();
    }

    Response updateOperationFees(OperationFeesModel requestModel) {
        return getHeader(getAdminToken())
                .body(requestModel)
                .put(SETTINGS_OPERATION_FEES_API_PATH)
                .thenReturn();
    }
}
