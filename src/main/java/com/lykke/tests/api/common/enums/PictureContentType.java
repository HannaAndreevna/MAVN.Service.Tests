package com.lykke.tests.api.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PictureContentType {
    PNG("image/png"),
    JPG("image/jpg"),
    JPEG("image/jpeg");

    @Getter
    private String value;

}
