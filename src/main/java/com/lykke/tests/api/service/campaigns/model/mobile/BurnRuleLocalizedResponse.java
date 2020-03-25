package com.lykke.tests.api.service.campaigns.model.mobile;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import com.lykke.tests.api.common.enums.BusinessVertical;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@PublicApi
public class BurnRuleLocalizedResponse {

    private String id;
    private String title;
    private String amountInTokens;
    private Double amountInCurrency;
    private boolean usePartnerCurrencyRate;
    private String currencyName;
    private String imageUrl;
    private String description;
    private String[] partnerIds;
    private BusinessVertical vertical;
    private Double price;
    private Date creationDate;
    private int order;

    public String getAmountInTokens() {
        return EMPTY.equalsIgnoreCase(amountInTokens) || null == amountInTokens
                ? "0.0"
                : Double.valueOf(amountInTokens).toString();
    }
}
