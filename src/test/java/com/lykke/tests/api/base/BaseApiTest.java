package com.lykke.tests.api.base;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;

import com.lykke.api.testing.allure.LykkeAllureRestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Timeout;

@Timeout(300)
public class BaseApiTest {

    @BeforeAll
    public static void baseSetup() {
        baseURI = "http:/"; // TODO: fix a proper way
        // enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.replaceFiltersWith(new LykkeAllureRestAssured());
    }

    @AfterAll
    public static void baseTearDown() {
        basePath = "";
    }
}
