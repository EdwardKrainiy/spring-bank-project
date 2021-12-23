package com.itech.service.transaction.impl;

import com.itech.model.dto.TransactionDto;
import com.itech.model.entity.Transaction;
import com.itech.repository.TransactionRepository;
import com.itech.service.transaction.TransactionService;
import com.itech.utils.exception.transaction.TransactionNotFoundException;
import com.itech.utils.mapper.TransactionDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionDtoMapper transactionDtoMapper;

    @Override
    public ResponseEntity<TransactionDto> findTransactionById(Long transactionId) {
        return ResponseEntity.ok(transactionDtoMapper.toDto(transactionRepository.getTransactionById(transactionId).orElseThrow(TransactionNotFoundException::new)));
    }

    @Override
    public ResponseEntity<List<TransactionDto>> findAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) throw new TransactionNotFoundException();

        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction : transactions) transactionDtos.add(transactionDtoMapper.toDto(transaction));

        return ResponseEntity.ok(transactionDtos);
    }
}
