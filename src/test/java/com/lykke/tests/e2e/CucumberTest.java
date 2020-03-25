package com.lykke.tests.e2e;

import cucumber.api.CucumberOptions;
//import cucumber.api.junit.jupiter.CucumberExtension;
//import io.cucumber.core.options.*;
//import io.cucumber.junit.*;
//import io.cucumber.java.*;
//import io.cucumber.junit.*;
//import cucumber.api.junit.Cucumber;
//import io.cucumber.junit.Cucumber.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

@CucumberOptions(plugin = {"pretty"})
//@ExtendWith(CucumberExtension.class)
//@ExtendWith(CucumberExtension .class)
public class CucumberTest {

    @TestFactory
    public Stream<DynamicTest> runTests(Stream<DynamicTest> scenarios) {
        List<DynamicTest> tests = scenarios.collect(Collectors.toList());
        return tests.stream();
    }
}
