package com.itech.service.transaction.impl;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Operation;
import com.itech.model.entity.Transaction;
import com.itech.model.entity.User;
import com.itech.model.enumeration.OperationType;
import com.itech.model.enumeration.TransactionStatus;
import com.itech.repository.AccountRepository;
import com.itech.repository.OperationRepository;
import com.itech.repository.TransactionRepository;
import com.itech.repository.UserRepository;
import com.itech.service.transaction.TransactionService;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.EntityValidationException;
import com.itech.utils.mapper.transaction.TransactionDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionDtoMapper transactionDtoMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public ResponseEntity<TransactionDto> findTransactionById(Long transactionId) {
        return ResponseEntity.ok(transactionDtoMapper.toDto(transactionRepository.getTransactionById(transactionId).orElseThrow(() -> new EntityNotFoundException("Transaction not found!"))));
    }

    @Override
    public ResponseEntity<List<TransactionDto>> findAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) throw new EntityNotFoundException("Transaction not found!");

        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction : transactions) transactionDtos.add(transactionDtoMapper.toDto(transaction));

        return ResponseEntity.ok(transactionDtos);
    }

    @Override
    public ResponseEntity<Long> createTransaction(@Validated TransactionCreateDto transactionCreateDto) {
        User foundUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        LocalDateTime currentDate = LocalDateTime.now();
        Transaction transaction = new Transaction();

        transaction.setUser(foundUser);
        transaction.setIssuedAt(java.sql.Timestamp.valueOf(currentDate));
        transaction.setStatus(TransactionStatus.IN_PROGRESS);

        Set<Operation> operations = new LinkedHashSet<>();

        for(OperationCreateDto operationCreateDto: transactionCreateDto.getOperations()){
            Operation operation = new Operation();
            operation.setAccount(accountRepository.findAccountByAccountNumber(operationCreateDto.getAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account not found!")));
            operation.setTransaction(transaction);
            operation.setOperationType(operationCreateDto.getOperationType());
            operation.setAmount(operationCreateDto.getAmount());
            operations.add(operationRepository.save(operation));
        }

        transaction.setOperations(operations);

        return ResponseEntity.ok(transactionRepository.save(transaction).getId());
    }

    @Override
    public ResponseEntity<Long> completeTransaction(Transaction transaction, Set<@Valid Operation> operations) {
        for(Operation operation: operations){
            LocalDate date = Instant.ofEpochMilli(transaction.getIssuedAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if(!date.isBefore(LocalDate.now().plusDays(1))) {
                transaction.setStatus(TransactionStatus.REJECTED);
                transactionRepository.save(transaction);
                throw new EntityValidationException("Time of transaction is over!");
            }
            switch (operation.getOperationType()){
                case DEBIT:
                    
                case CREDIT:
            }

        }
        return ResponseEntity.ok(Long.parseLong("-1"));
    }
}
