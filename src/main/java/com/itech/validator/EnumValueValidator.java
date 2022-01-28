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

  /**
   * initialize method. Converts Enum to list of Strings.
   *
   * @param annotation Enum we want to convert.
   */
  @Override
  public void initialize(EnumValue annotation) {
    enumValues =
        Stream.of(annotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());
  }

  /**
   * isValid method. Compares our value with Strings of ENUMS and returns boolean value.
   *
   * @param value Value we need to compare.
   * @param context ValidatorContext.
   * @return Boolean value of method. FALSE - ENUM not contains value. TRUE - ENUM contains value.
   */
  @Override
  public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
    if (value == null) return false;
    return enumValues.contains(value.toString().toUpperCase(Locale.ROOT));
  }
}
