package com.lykke.tests.api.service.crosschainwalletlinker.model;

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
public enum LinkingError {
    NONE("None", "No errors"),
    INVALID_CUSTOMER_ID("InvalidCustomerId", "The customer id provided is not valid"),
    LINKING_REQUEST_ALREADY_EXISTS("LinkingRequestAlreadyExists", "The linking/unlinking request already exists"),
    CUSTOMER_WALLET_MISSING("CustomerWalletMissing", "The customer's private wallet address is not assigned"),
    LINKING_REQUEST_DOES_NOT_EXIST("LinkingRequestDoesNotExist", "The linking/unlinking request does not exist yet"),
    INVALID_PUBLIC_ADDRESS("InvalidPublicAddress", "The public address is not valid"),
    INVALID_SIGNATURE("InvalidSignature", "The signature is not valid"),
    LINKING_REQUEST_ALREADY_APPROVED("LinkingRequestAlreadyApproved", "The linking request has already been approved"),
    INVALID_PRIVATE_ADDRESS("InvalidPrivateAddress", "The private address is not valid"),
    CANNOT_DELETE_LINKING_REQUEST_WHILE_CONFIRMING("CannotDeleteLinkingRequestWhileConfirming",
            "The linking request can't be deleted while it is being processed in blockchain"),
    NOT_ENOUGH_FUNDS("NotEnoughFunds", "The balance is not enough to approve linking request if fee > 0"),
    CUSTOMER_DOES_NOT_EXIST("CustomerDoesNotExist", "There is no customer with this customerId"),
    CUSTOMER_WALLET_BLOCKED("CustomerWalletBlocked", "Customer's wallet is blocked");

    private static Map<String, LinkingError> FORMAT_MAP =
            Stream.of(LinkingError.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String comment;

    @JsonCreator
    public static LinkingError fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
