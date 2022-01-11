package com.itech.validator;


import com.itech.validator.annotation.EnumValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Enum<?>> {
    private List<? extends Enum<?>> enumValues;

    @Override
    public void initialize(EnumValue annotation) {
        enumValues = Stream.of(annotation.enumClass().getEnumConstants()).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }

        return enumValues.contains(value);
    }
}