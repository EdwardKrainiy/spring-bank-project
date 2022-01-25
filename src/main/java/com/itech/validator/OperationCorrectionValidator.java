package com.itech.validator;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.enumeration.OperationType;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.literal.ExceptionMessageText;
import com.itech.utils.literal.LogMessageText;
import com.itech.validator.annotation.IsOperationCorrect;
import lombok.extern.log4j.Log4j2;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.Set;

@Log4j2
public class OperationCorrectionValidator implements ConstraintValidator<IsOperationCorrect, Set<OperationCreateDto>> {

    @Override
    public boolean isValid(Set<OperationCreateDto> dtoOperations, ConstraintValidatorContext context) {
        boolean isNumbersEquals = false;
        boolean isCreditExists = false;
        boolean isDebitExists = false;
        String accountNumberToCheck;
        Optional<OperationCreateDto> firstOperationOptional = dtoOperations.stream().findFirst();

        if (!firstOperationOptional.isPresent()) {
            log.error(LogMessageText.OPERATIONS_ARE_EMPTY_LOG);
            throw new EntityNotFoundException(ExceptionMessageText.OPERATIONS_ARE_EMPTY);
        }
        accountNumberToCheck = firstOperationOptional.get().getAccountNumber();

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