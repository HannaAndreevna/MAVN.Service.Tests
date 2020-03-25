package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.PathConsts.CustomerApiEndpoint.GENERATE_RESET_PW_LINK_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;

import com.lykke.tests.api.base.Paths;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class GeneratePwLinkUtils {

    Response generateResetPasswordLink(String email) {
        return getHeader()
                .body(createEmailObj(email))
                .post(Paths.CUSTOMER_API_PATH + GENERATE_RESET_PW_LINK_PATH.getPath());
    }

    private static JSONObject createEmailObj(String emailAddress) {
        JSONObject emailBody = new JSONObject();
        emailBody.put("Email", emailAddress);
        return emailBody;
    }
}
