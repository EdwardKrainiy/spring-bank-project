package com.itech.validator;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.utils.exception.EntityNotFoundException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class OperationCorrectionValidator implements ConstraintValidator<IsOperationCorrect, Set<OperationCreateDto>> {

    @Override
    public boolean isValid(Set<OperationCreateDto> dtoOperations, ConstraintValidatorContext context) {
        Boolean isNumbersEquals = false;
        Boolean isCreditExists = false;
        Boolean isDebitExists = false;
        String accountNumberToCheck = dtoOperations.stream().findFirst().orElseThrow(() -> new EntityNotFoundException("Operations not found!")).getAccountNumber();

        if (dtoOperations.size() == 2) {
            if (dtoOperations.stream().filter(operationCreateDto -> operationCreateDto.getAccountNumber().equals(accountNumberToCheck)).count() == 2) {
                isNumbersEquals = true;
            }
            for (OperationCreateDto operationCreateDto : dtoOperations) {
                if (operationCreateDto.getOperationType().equals(com.itech.model.enumeration.OperationType.CREDIT.name())) {
                    isCreditExists = true;
                }
                if (operationCreateDto.getOperationType().equals(com.itech.model.enumeration.OperationType.DEBIT.name())) {
                    isDebitExists = true;
                }
            }
            if (isCreditExists && isDebitExists && isNumbersEquals) {
                return false;
            }
        }
        return true;
    }
}