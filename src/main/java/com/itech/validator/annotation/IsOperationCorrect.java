package com.itech.validator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.itech.validator.OperationCorrectionValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * IsOperationCorrect annotation. Checks validity and correctness of operations.
 *
 * @author Edvard Krainiy on 01/19/2022
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = OperationCorrectionValidator.class)
public @interface IsOperationCorrect {
  String message();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
