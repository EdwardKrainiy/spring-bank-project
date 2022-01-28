package com.itech.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;

/**
 * Enum, which contains all currencies we need in app.
 *
 * @author Edvard Krainiy on 16/12/2021
 */
@RequiredArgsConstructor
@Getter
public enum Currency {
  PLN(CountryCode.PL),
  GBP(CountryCode.GB),
  EUR(CountryCode.DE);

  private final CountryCode countryCode;
}
