package com.itech.validator;

import com.itech.validator.annotation.EnumValue;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * EnumValueValidator class. Implements EnumValue annotation.
 *
 * @author Edvard Krainiy on 01/19/2022
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, CharSequence> {
  private List<String> enumValues;

  @Override
  public void initialize(EnumValue annotation) {
    enumValues =
        Stream.of(annotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());
  }

  @Override
  public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
    if (value == null) return false;
    return enumValues.contains(value.toString().toUpperCase(Locale.ROOT));
  }
}
