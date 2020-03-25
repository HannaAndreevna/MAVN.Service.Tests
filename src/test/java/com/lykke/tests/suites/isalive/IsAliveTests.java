package com.lykke.tests.suites.isalive;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.base.PathConsts.getIsAlivePath;
import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.tests.api.base.PathConsts.ComponentBaseUrl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

public class IsAliveTests extends BaseApiTest {

    private static final String NAME_FIELD = "name";

    @ParameterizedTest
    @EnumSource(value = ComponentBaseUrl.class, mode = Mode.EXCLUDE, names = {"PERSONAL_DATA_COMPONENT_URL",
            "WALLET_API_COMPONENT_URL", "MVN_UBE_INTEGRATION_COMPONENT_URL", "CREDENTIALS_ADMIN_COMPONENT_URL",
            "SMS_PROVIDER_MOCK"})
    @Tag(SMOKE_TEST)
    void shouldBeAlive(ComponentBaseUrl baseUrl) {

        getHeader()
                .get(getIsAlivePath(baseUrl))
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(NAME_FIELD, Matchers.equalTo(baseUrl.getNamespace()));

    }
}
