package com.itech.service.transaction;

import com.itech.model.dto.transaction.TransactionDto;

import java.util.List;

/**
 * TransactionService interface. Provides us different methods to work with Transaction objects on Service layer.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
public interface TransactionService {
    TransactionDto findTransactionById(Long transactionId);

    List<TransactionDto> findAllTransactions();

    TransactionDto createTransaction(String creationRequestJson);
}
