package com.itech.model.enumeration;

import org.iban4j.CountryCode;

/**
 * Enum, which contains all currencies we need in app.
 *
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

