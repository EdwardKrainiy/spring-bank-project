package com.itech.service.transaction;

import com.itech.model.entity.Operation;
import com.itech.model.entity.Transaction;
import com.itech.repository.TransactionRepository;
import com.itech.utils.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

/**
 * This class contains additional required methods to create Transaction.
 *
 * @author Edvard Krainiy on 12/27/2021
 */
@Component
public class TransactionServiceUtil {
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * changeAccountAmount method. Changes amount on all accounts.
     *
     * @param operations Set of operations, which are required to obtain Account objects.
     * @param isDtoValid Flag, that shows us validity of Dto.
     * @throws EntityValidationException If isDtoValid is false.
     */

    @Transactional
    public void changeAccountAmount(Set<Operation> operations, boolean isDtoValid) throws ValidationException {
        for (Operation operation : operations) {
            switch (operation.getOperationType()) {
                case CREDIT:
                    operation.getAccount().setAmount(operation.getAccount().getAmount() + operation.getAmount());
                    break;
                case DEBIT:
                    operation.getAccount().setAmount(operation.getAccount().getAmount() - operation.getAmount());
                    break;
            }
        }

        if (!isDtoValid) {
            throw new ValidationException("Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.");
        }
    }

    /**
     * checkRequestDtoValidity method.
     *
     * @param operations Set of operations, which are required to obtain Account objects.
     * @param transaction Transaction object, which we need to write in DB.
     * @return Boolean Is dto valid flag.
     */

    public boolean checkRequestDtoValidity(Set<Operation> operations, Transaction transaction){
        double sumOfAmounts = 0;

        boolean isDebitOperationsExists = false;
        boolean isCreditOperationsExists = false;

        for (Operation operation : operations) {
            LocalDate date = Instant.ofEpochMilli(transaction.getIssuedAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!date.isBefore(LocalDate.now().plusDays(1))) {
                transactionRepository.save(transaction);
                throw new ValidationException("Time of transaction is over!");
            }

            switch (operation.getOperationType()) {
                case CREDIT:
                    isDebitOperationsExists = true;
                    sumOfAmounts = sumOfAmounts + operation.getAmount();
                    break;
                case DEBIT:
                    if (operation.getAccount().getAmount() - operation.getAmount() < 0) {
                        transactionRepository.save(transaction);
                        throw new ValidationException("DEBIT amount is more than stored in this account.");
                    }

                    isCreditOperationsExists = true;
                    sumOfAmounts = sumOfAmounts - operation.getAmount();
                    break;
            }
        }
        return isCreditOperationsExists && isDebitOperationsExists && sumOfAmounts == 0;
    }
}
