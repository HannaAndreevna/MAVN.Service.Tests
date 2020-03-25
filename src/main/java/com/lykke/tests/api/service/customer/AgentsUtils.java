package com.lykke.tests.api.service.customer;

import static com.lykke.tests.api.base.Paths.CUSTOMER_API_AGENTS_PATH;
import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static org.apache.http.HttpStatus.SC_OK;

import com.lykke.tests.api.service.customer.model.agents.AgentRegistrationRequestModel;
import com.lykke.tests.api.service.customer.model.agents.AgentsModel;
import com.lykke.tests.api.service.customer.model.agents.AgentsRequest;
import com.lykke.tests.api.service.customer.model.agents.AgentsValidationErrorResponseModel;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;

@UtilityClass
public class AgentsUtils {

    public static final String FIRST_NAME_FIELD = "FirstName";
    public static final String LAST_NAME_FIELD = "LastName";
    public static final String PHONE_NUMBER_FIELD = "PhoneNumber";

    public Response createAgent(AgentRegistrationRequestModel requestParameters) {
        return getHeader(requestParameters.getToken())
                .body(requestParameters)
                .post(CUSTOMER_API_AGENTS_PATH);
    }

    public AgentsValidationErrorResponseModel createAgentErrorResponse(AgentsRequest requestParameters) {
        return getHeader(requestParameters.getToken())
                .body(createAgentRequestBody(requestParameters))
                .post(CUSTOMER_API_AGENTS_PATH)
                .then()
                .extract()
                .as(AgentsValidationErrorResponseModel.class);
    }

    public AgentsModel getAgents(String token) {
        return getHeader(token)
                .get(CUSTOMER_API_AGENTS_PATH)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(AgentsModel.class);
    }

    private static JSONObject createAgentRequestBody(AgentsRequest requestParameters) {
        JSONObject emailBody = new JSONObject();
        emailBody.put(FIRST_NAME_FIELD, requestParameters.getFirstName());
        emailBody.put(LAST_NAME_FIELD, requestParameters.getLastName());
        emailBody.put(PHONE_NUMBER_FIELD, requestParameters.getPhoneNumber());
        return emailBody;
    }


}
