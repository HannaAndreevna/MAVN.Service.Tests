package com.lykke.tests.api.service.vouchers.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public enum VoucherStatus {
    NONE("None", "Unspecified status."),
    IN_STOCK("InStock", "Indicates that the voucher is stock. "),
    RESERVED("Reserved", "Indicated that the voucher reserved by customer and waiting for payment."),
    SOLD("Sold", "Indicates that the voucher bought by a customer. ");

    private static Map<String, VoucherStatus> FORMAT_MAP =
            Stream.of(VoucherStatus.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static VoucherStatus fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
