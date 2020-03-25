package com.lykke.tests.api.service.campaigns.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.NetClassName;
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
@NetClassName("CampaignServiceErrorCodes")
public enum CampaignServiceErrorCode {
    NONE("None", "Empty code"),
    ENTITY_NOT_FOUND("EntityNotFound", "Entity with the provided id does not exists"),
    GUID_CAN_NOT_BE_PARSED("GuidCanNotBeParsed", "Passed values can not be parsed to guid"),
    ENTITY_NOT_VALID("EntityNotValid", "Entity not valid error code"),
    ENTITY_ALREADY_EXISTS("EntityAlreadyExists", "Entity already exists error code"),
    NOT_VALID_FILE_FORMAT("NotValidFileFormat", "File's format not valid"),
    NOT_VALID_RULE_CONTENT_TYPE("NotValidRuleContentType", "Passed rule content type is not valid");

    private static Map<String, CampaignServiceErrorCode> FORMAT_MAP =
            Stream.of(CampaignServiceErrorCode.values())
                    .collect(toMap(c -> c.getCode(), Function.identity()));
    @Getter
    private String code;

    @Getter
    private String description;

    @JsonCreator
    public static CampaignServiceErrorCode fromString(String value) {
        return Optional
                .ofNullable(FORMAT_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
