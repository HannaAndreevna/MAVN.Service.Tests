package com.lykke.tests.api.base;

import static com.lykke.tests.api.common.ConfigUtils.getBackendPassword;
import static com.lykke.tests.api.common.ConfigUtils.getBackendUsername;
import static com.lykke.tests.api.service.admin.GetAdminsUtils.getAdminUserId;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Credentials {

    public static final String ADMIN_TEST_USER_EMAIL = getBackendUsername();
    public static final String ADMIN_TEST_USER_PW = getBackendPassword();
    public static final String ADMIN_TEST_USER_ID = getAdminUserId(ADMIN_TEST_USER_EMAIL);
}
