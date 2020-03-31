package io.hepp.cov2words.common.constant.originstamp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Defines the currencies used by OriginStamp.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@AllArgsConstructor
@Getter
public enum OriginStampCurrency {
    BITCOIN(0, "Bitcoin"),
    ETHEREUM(1, "Ethereum"),
    AION(2, "Aion"),
    SUEDKURIER(100, "Suedkurier"),
    UNKNOWN(-1, "Unknown");

    private int currencyId;
    private String currencyName;

    /**
     * Returns the currency enum for a certain currency ID.
     */
    public static OriginStampCurrency getCurrencyForId(int currencyId) {
        return Arrays.stream(OriginStampCurrency.values())
                .filter(currency -> currency.getCurrencyId() == currencyId)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
