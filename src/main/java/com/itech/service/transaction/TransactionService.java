package com.itech.service.transaction;

import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Operation;
import com.itech.model.entity.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

/**
 * TransactionService interface. Provides us different methods to work with Transaction objects on Service layer.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
public interface TransactionService {
    TransactionDto findTransactionById(Long transactionId);

    List<TransactionDto> findAllTransactions();

    Long createTransaction(TransactionCreateDto transactionCreateDto);

}
