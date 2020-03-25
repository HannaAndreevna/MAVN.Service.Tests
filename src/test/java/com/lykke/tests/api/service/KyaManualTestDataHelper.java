package com.lykke.tests.api.service;

import static com.lykke.api.testing.api.base.RequestHeader.getHeader;
import static com.lykke.tests.api.service.customer.LoginLogoutCustomerUtils.getUserToken;
import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerCustomer;

import com.google.common.util.concurrent.Uninterruptibles;
import com.lykke.tests.api.base.BaseApiTest;
import com.lykke.api.testing.api.common.FakerUtils;
import com.lykke.api.testing.api.common.GenerateUtils;
import com.lykke.tests.api.service.customermanagement.model.register.RegistrationRequestModel;
import io.restassured.response.Response;

import java.util.concurrent.TimeUnit;

import lombok.var;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Created as a manual helper")
public class KyaManualTestDataHelper extends BaseApiTest {

    // register
    // login
    // apply for KYA
    // lead referral

    private static final String ACME = "Acme";
    private static final String NUMBER = "12345678";
    private static final String VALID_SWIFT = "SBRFRUXX";
    private static final String VALID_IBAN = "GB33BUKB20201555555555";
    private static final String VALID_IMAGE_SHA = "d821811a0e5d528687cc88b398f117f610946a93937b541bfdb691f765558fc5";

    @Test
    void kya() {
        String email = FakerUtils.firstName.toLowerCase() + FakerUtils.lastName.toLowerCase() + "456@example.com";
        String pw = "P@ssword1!";
        var customer = new RegistrationRequestModel();
        customer.setEmail(email);
        customer.setPassword(pw);
        String customerId = registerCustomer(customer);
        String customerToken = getUserToken(customer);

        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);

        JSONObject bankObject = new JSONObject();
        bankObject.put("BeneficiaryName", GenerateUtils.generateName(200));
        bankObject.put("BankName", ACME);
        bankObject.put("BankBranch", ACME);
        bankObject.put("AccountNumber", NUMBER);
        bankObject.put("BankAddress", ACME);
        bankObject.put("BankBranchCountryId", 1);
        bankObject.put("Iban", VALID_IBAN);
        bankObject.put("Swift", VALID_SWIFT);

        JSONObject imagesObject = new JSONObject();
        imagesObject.put("DocumentType", "Passport");
        imagesObject.put("Name", ACME + ".jpg");
        imagesObject.put("Content", VALID_IMAGE_SHA); // big image

        JSONArray imagesArray = new JSONArray();
        imagesArray.add(imagesObject);

        JSONObject agentObj = new JSONObject();
        agentObj.put("FirstName", FakerUtils.firstName);
        agentObj.put("LastName", FakerUtils.lastName);
        agentObj.put("PhoneNumber", NUMBER);
        agentObj.put("CountryPhoneCodeId", 1);
        agentObj.put("CountryOfResidenceId", 1);
        agentObj.put("Note", ACME);
        agentObj.put("BankInfo", bankObject);
        agentObj.put("Images", imagesArray);

        submitKya(customerToken, agentObj)
                .then()
                .log()
                .all();
    }

    // AGENT
    // REFER LEAD
    // LEAD CONFIRMATOON EMAIL
    // ANOTHER LEAD WITH SAME PHONE NOMBER NUMBER && email or phone number

    @Test
    void kyaWithPresetUser() {
        String customerToken = getUserToken("izabelagent@example.com", "P@ssword1!");

        JSONObject bankObject = new JSONObject();
        bankObject.put("BeneficiaryName", GenerateUtils.generateName(200)); // checked 200 works - with only letters
        bankObject.put("BankName", GenerateUtils.generateName(90)); // checked 90 works - with only letters
        bankObject.put("BankBranch", GenerateUtils.generateName(90));
        bankObject.put("AccountNumber", NUMBER);
        bankObject.put("BankAddress", ACME);
        bankObject.put("BankBranchCountryId", 1);
        bankObject.put("Iban", VALID_IBAN);
        bankObject.put("Swift", VALID_SWIFT);

        JSONObject imagesObject = new JSONObject();
        imagesObject.put("DocumentType", "Passport");
        imagesObject.put("Name", ACME + ".jpg");
        imagesObject.put("Content", VALID_IMAGE_SHA); // big image

        JSONArray imagesArray = new JSONArray();
        imagesArray.add(imagesObject);

        JSONObject agentObj = new JSONObject();
        agentObj.put("FirstName", FakerUtils.firstName);
        agentObj.put("LastName", FakerUtils.lastName);
        agentObj.put("PhoneNumber", NUMBER);
        agentObj.put("CountryPhoneCodeId", 1);
        agentObj.put("CountryOfResidenceId", 1);
        agentObj.put("Note", ACME);
        agentObj.put("BankInfo", bankObject);
        agentObj.put("Images", imagesArray);

        submitKya(customerToken, agentObj)
                .then()
                .log()
                .all();
    }

    public Response submitKya(String token, JSONObject obj) {
        return getHeader(token)
                .body(obj)
                .post("http://customer-api.services.svc.cluster.local/api/agents");
    }

    /*
    1. Lead referral - lead deleted from SF - hangs in "Accepted" @Ivelin
    2. How to reject lead from SF? @Ivelin
    3. TODO: 1310 cases
     */

    @Test
    void passwordValidationRuleset() {
        String email = FakerUtils.firstName.toLowerCase() + FakerUtils.lastName.toLowerCase() + "456@example.com";
        String pw = "P@ssword1";
        var customer = new RegistrationRequestModel();
        customer.setEmail(email);
        customer.setPassword(pw);
        String customerId = registerCustomer(customer);
    }
}
