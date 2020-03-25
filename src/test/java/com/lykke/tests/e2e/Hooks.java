package com.lykke.tests.e2e;

import com.lykke.tests.api.base.BaseApiTest;
import cucumber.api.java8.En;

public class Hooks implements En {

    public Hooks() {
        Before(0, () -> BaseApiTest.baseSetup());
        After(0, () -> BaseApiTest.baseTearDown());
    }
}
