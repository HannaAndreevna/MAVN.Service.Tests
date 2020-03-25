package com.lykke.tests.api.service.admin.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PagedModel {

    private String nextPage;
    private String[] previousPages;
    private int currentPage;
    private int navigatePage;
}
