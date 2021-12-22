package com.itech.utils;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

/**
 * IbanGenerator class, which contains methods to generate random IBAN by CountryCode.
 *
 * @author Edvard Krainiy on 12/21/2021
 */

@Component
public class IbanGenerator {

    /**
     * generateIban method. Generates random IBAN by CountryCode.
     *
     * @param countryCode CountryCode if IBAN we want to create.
     * @return String Obtained IBAN.
     */
    public String generateIban(CountryCode countryCode) {
        return Iban.random(countryCode).toString();
    }
}
