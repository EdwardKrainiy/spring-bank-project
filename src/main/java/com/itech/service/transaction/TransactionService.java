package com.itech.service.transaction;

import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Operation;
import com.itech.model.entity.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
public interface TransactionService {
    ResponseEntity<TransactionDto> findTransactionById(Long transactionId);

    ResponseEntity<List<TransactionDto>> findAllTransactions();

    ResponseEntity<Long> createTransaction(TransactionCreateDto transactionCreateDto);

    ResponseEntity<Long> completeTransaction(Transaction transaction, Set<Operation> operations);
}
