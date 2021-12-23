package com.itech.service.transaction;

import com.itech.model.dto.TransactionDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
public interface TransactionService {
    ResponseEntity<TransactionDto> findTransactionById(Long transactionId);

    ResponseEntity<List<TransactionDto>> findAllTransactions();
}
