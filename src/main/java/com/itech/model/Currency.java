package com.itech.model;

import org.iban4j.CountryCode;

/**
 * @author Edvard Krainiy on 16/12/2021
 */
public enum Currency {
    PLN(CountryCode.PL),
    GBP(CountryCode.GB),
    EUR(CountryCode.DE);

    private final CountryCode countryCode;

    Currency(CountryCode countryCode) {
        this.countryCode = countryCode;
    }

    public CountryCode getCountryCode() {
        return this.countryCode;
    }
}

