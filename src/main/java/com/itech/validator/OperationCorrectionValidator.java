package com.itech.validator;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.enumeration.OperationType;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.validator.annotation.IsOperationCorrect;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class OperationCorrectionValidator implements ConstraintValidator<IsOperationCorrect, Set<OperationCreateDto>> {

    @Override
    public boolean isValid(Set<OperationCreateDto> dtoOperations, ConstraintValidatorContext context) {
        boolean isNumbersEquals = false;
        boolean isCreditExists = false;
        boolean isDebitExists = false;
        String accountNumberToCheck = dtoOperations.stream().findFirst().orElseThrow(() -> new EntityNotFoundException("Operations not found!")).getAccountNumber();

        if (dtoOperations.size() == 2) {
            if (dtoOperations.stream().filter(operationCreateDto -> operationCreateDto.getAccountNumber().equals(accountNumberToCheck)).count() == 2) {
                isNumbersEquals = true;
            }
            for (OperationCreateDto operationCreateDto : dtoOperations) {
                if (operationCreateDto.getOperationType().equals(OperationType.CREDIT.name())) {
                    isCreditExists = true;
                }
                if (operationCreateDto.getOperationType().equals(OperationType.DEBIT.name())) {
                    isDebitExists = true;
                }
            }
            return !isCreditExists || !isDebitExists || !isNumbersEquals;
        }
        return true;
    }
}