package com.itech.validator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.itech.validator.EnumValueValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * EnumValue annotation. Compares String with ENUM values.
 *
 * @author Edvard Krainiy on 01/19/2022
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {
  String message() default "Incorrect value of Enumeration!";

  Class<? extends Enum<?>> enumClass();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
