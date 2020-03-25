package com.lykke.tests.api.service.dictionaries.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonInformationPropertiesModel {

    private String supportPhoneNumber;
    private String facebookLink;
    private String twitterLink;
    private String instagramLink;
    private String linkedInLink;
    private String youTubeLink;
    private String downloadAppLink;
    private String termsAndConditionLink;
    private String privacyPolicyLink;
    private String unsubscribeLink;
    private String downloadAndroidAppLink;
    private String downloadIsoAppLink;
    private String supportEmailAddress;
}
