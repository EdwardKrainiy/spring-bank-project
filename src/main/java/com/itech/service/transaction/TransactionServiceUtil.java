package com.itech.service.transaction;

import com.itech.model.entity.Operation;
import com.itech.model.entity.Transaction;
import com.itech.model.enumeration.OperationType;
import com.itech.model.enumeration.Status;
import com.itech.repository.TransactionRepository;
import com.itech.utils.exception.ChangeAccountAmountException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.literal.ExceptionMessageText;
import com.itech.utils.literal.LogMessageText;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class contains additional required methods to create Transaction.
 *
 * @author Edvard Krainiy on 12/27/2021
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class TransactionServiceUtil {
  private final TransactionRepository transactionRepository;

  /**
   * changeAccountAmount method. Changes amount on all accounts.
   *
   * @param operations Set of operations, which are required to obtain Account objects.
   * @throws ValidationException If isDtoValid is false.
   * @throws ChangeAccountAmountException If credit is more than stored on account.
   */
  @Transactional
  public void changeAccountAmount(Set<Operation> operations) throws ChangeAccountAmountException {
    for (Operation operation : operations) {
      if (operation.getOperationType().equals(OperationType.CREDIT)
          && operation.getAccount().getAmount() - operation.getAmount() >= 0) {
        operation
            .getAccount()
            .setAmount(operation.getAccount().getAmount() - operation.getAmount());
      } else if (operation.getOperationType().equals(OperationType.DEBIT)) {
        operation
            .getAccount()
            .setAmount(operation.getAccount().getAmount() + operation.getAmount());
      } else {
        log.error(
            String.format(
                LogMessageText.CREDIT_IS_MORE_THAN_STORED_ON_ACCOUNT_LOG,
                operation.getAccount().getId()));
        throw new ChangeAccountAmountException(
            ExceptionMessageText.CREDIT_IS_MORE_THAN_STORED_ON_ACCOUNT);
      }
    }
  }

  /**
   * checkRequestDtoValidity method.
   *
   * @param operations Set of operations, which are required to obtain Account objects.
   * @param transaction Transaction object, which we need to write in DB.
   * @return Boolean Is dto valid flag.
   */
  public boolean checkRequestDtoValidity(Set<Operation> operations, Transaction transaction) {
    LocalDate date = transaction.getIssuedAt().atZone(ZoneId.systemDefault()).toLocalDate();
    if (date.isBefore(LocalDate.now().minusDays(1))) {
      transaction.setStatus(Status.EXPIRED);
      transactionRepository.save(transaction);
      log.error(
          String.format(
              LogMessageText.TRANSACTION_CREATION_REQUEST_EXPIRED_LOG, transaction.getId()));
      throw new ValidationException(ExceptionMessageText.CREATION_REQUEST_EXPIRED);
    }

    boolean areOperationTypesCorrect = checkOperationTypes(operations);
    boolean isSumOfAmountsEqualsZero = checkSumOfOperationAmounts(operations);

    return areOperationTypesCorrect && isSumOfAmountsEqualsZero;
  }

  private boolean checkOperationTypes(Set<Operation> operations) {
    boolean isDebitOperationsExists = false;
    boolean isCreditOperationsExists = false;

    for (Operation operation : operations) {
      if (operation.getOperationType().equals(OperationType.CREDIT)) {
        isCreditOperationsExists = true;
      } else {
        isDebitOperationsExists = true;
      }
    }
    return (isCreditOperationsExists && isDebitOperationsExists);
  }

  private boolean checkSumOfOperationAmounts(Set<Operation> operations) {
    double sumOfAmounts = 0;

    for (Operation operation : operations) {
      if (operation.getOperationType().equals(OperationType.CREDIT)) {
        sumOfAmounts -= operation.getAmount();
      } else {
        sumOfAmounts += operation.getAmount();
      }
    }
    return sumOfAmounts == 0;
  }
}
