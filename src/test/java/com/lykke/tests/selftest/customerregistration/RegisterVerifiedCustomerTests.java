package com.lykke.tests.selftest.customerregistration;

import static com.lykke.tests.api.service.customermanagement.RegisterCustomerUtils.registerDefaultVerifiedCustomer;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lykke.tests.api.base.BaseApiTest;
import java.util.HashSet;
import java.util.stream.IntStream;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RegisterVerifiedCustomerTests extends BaseApiTest {

    @Test
    void shouldRegisterVerifiedCustomers() {
        final int numberOfCustomers = 20;
        val actualResult = IntStream.range(0, numberOfCustomers)
                .mapToObj(index -> registerDefaultVerifiedCustomer())
                .collect(toMap(x -> x.getCustomerId(), x -> x.getEmail()));

        actualResult.entrySet().stream()
                .forEach(item -> System.out.println(item.getKey() + ", " + item.getValue()));

        assertAll(
                () -> assertEquals(numberOfCustomers, actualResult.entrySet().size()),
                () -> assertEquals(numberOfCustomers, new HashSet<>(actualResult.keySet()).size()),
                () -> assertEquals(numberOfCustomers, actualResult.size())
        );
    }
}
