package com.lykke.tests.api.service.admin.model;

import com.lykke.tests.api.common.enums.Localization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class MobileContentCreateRequest {

    private Localization mobileLanguage;
    private String title;
    private String description;
    private String imageBlobUrl;
}
