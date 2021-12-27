package com.itech.service.transaction.impl;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.Operation;
import com.itech.model.entity.Transaction;
import com.itech.model.entity.User;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<Long> createTransaction(TransactionCreateDto transactionCreateDto) {
        User foundUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        LocalDateTime currentDate = LocalDateTime.now();
        Transaction transaction = new Transaction();

        transaction.setUser(foundUser);
        transaction.setIssuedAt(java.sql.Timestamp.valueOf(currentDate));
        transaction.setStatus(TransactionStatus.IN_PROGRESS);

        Set<Operation> operations = new LinkedHashSet<>();

        Set<OperationCreateDto> dtoOperations = transactionCreateDto.getOperations();

        for (OperationCreateDto operationCreateDto : dtoOperations) {
            Operation operation = new Operation();
            operation.setAccount(accountRepository.findAccountByAccountNumber(operationCreateDto.getAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account not found!")));
            operation.setTransaction(transaction);
            operation.setOperationType(operationCreateDto.getOperationType());
            operation.setAmount(operationCreateDto.getAmount());
            operations.add(operationRepository.save(operation));
        }

        transaction.setOperations(operations);

        return completeTransaction(transaction, operations);
    }

    @Override
    public ResponseEntity<Long> completeTransaction(Transaction transaction, Set<Operation> operations) {
        transaction.setStatus(TransactionStatus.REJECTED);
        double sumOfAmounts = 0;

        boolean isDebitOperationsExists = false;
        boolean isCreditOperationsExists = false;

        List<Account> accounts = new ArrayList<>();

        for (Operation operation : operations) {
            LocalDate date = Instant.ofEpochMilli(transaction.getIssuedAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!date.isBefore(LocalDate.now().plusDays(1))) {
                transactionRepository.save(transaction);
                throw new EntityValidationException("Time of transaction is over!");
            }

            Account operationAccount;

            switch (operation.getOperationType()) {
                case CREDIT:
                    operationAccount = operation.getAccount().clone();
                    operationAccount.setAmount(operationAccount.getAmount() + operation.getAmount());
                    accounts.add(operationAccount);

                    isDebitOperationsExists = true;
                    sumOfAmounts = sumOfAmounts + operation.getAmount();
                    break;
                case DEBIT:
                    operationAccount = operation.getAccount().clone();
                    if (operationAccount.getAmount() - operation.getAmount() < 0) {
                        transactionRepository.save(transaction);
                        throw new EntityValidationException("DEBIT amount is more than stored in this account.");
                    }
                    operationAccount.setAmount(operationAccount.getAmount() - operation.getAmount());
                    accounts.add(operationAccount);

                    isCreditOperationsExists = true;
                    sumOfAmounts = sumOfAmounts - operation.getAmount();
                    break;
            }
        }

        if (!isCreditOperationsExists || !isDebitOperationsExists || sumOfAmounts != 0) {
            transactionRepository.save(transaction);
            throw new EntityValidationException("Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.");
        }

        accountRepository.saveAll(accounts);

        transaction.setStatus(TransactionStatus.CREATED);

        return ResponseEntity.status(201).body(transactionRepository.save(transaction).getId());
    }
}
