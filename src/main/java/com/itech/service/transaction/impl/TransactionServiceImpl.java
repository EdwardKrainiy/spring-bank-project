package com.itech.service.transaction.impl;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.dto.operation.OperationDto;
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
import com.itech.service.transaction.TransactionServiceUtil;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.operation.OperationDtoMapper;
import com.itech.utils.mapper.transaction.TransactionDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of TransactionService interface. Provides us different methods of Service layer to work with Repository layer of Transaction objects.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionDtoMapper transactionDtoMapper;

    @Autowired
    private OperationDtoMapper operationDtoMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private TransactionServiceUtil transactionServiceUtil;



    /**
     * findTransactionById method. Finds transaction by transactionId.
     *
     * @param transactionId If of transaction we need to find.
     * @return TransactionDto Found transactionDto object.
     */

    @Override
    public TransactionDto findTransactionById(Long transactionId) {
        return transactionDtoMapper.toDto(transactionRepository.getTransactionById(transactionId).orElseThrow(() -> new EntityNotFoundException("Transaction not found!")));
    }

    /**
     * findAllTransactions method. Finds all transactions, stored in DB.
     *
     * @return List<TransactionDto> List of all found transactionDto objects.
     */

    @Override
    public List<TransactionDto> findAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) throw new EntityNotFoundException("Transaction not found!");

        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactions.forEach(transaction -> transactionDtos.add(transactionDtoMapper.toDto(transaction)));

        return transactionDtos;
    }

    /**
     * createTransaction method. Creates Transaction from transactionCreateDto.
     *
     * @param transactionCreateDto Object, from which we want to create Transaction.
     * @return Long id of created Transaction.
     */

    @Override
    public TransactionDto createTransaction(TransactionCreateDto transactionCreateDto) {
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

            if(operationCreateDto.getOperationType().equals("DEBIT") || operationCreateDto.getOperationType().equals("CREDIT"))
            {
                operation.setOperationType(OperationType.valueOf(operationCreateDto.getOperationType()));

            } else throw new ValidationException("Incorrect Operation Type!");

            operation.setAmount(operationCreateDto.getAmount());
            operations.add(operation);
        }

        if(!transactionServiceUtil.checkRequestDtoValidity(operations, transaction)) throw new ValidationException("Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.");


        operationRepository.saveAll(operations);

        transaction.setOperations(operations);

        return completeTransaction(transaction, operations);
    }

    /**
     * completeTransaction method. Provides us
     *
     * @param transaction Transaction object, which we need to write to DB.
     * @param operations Set of operations we need to check request dto validity and to change account amount.
     * @return TransactionDto Dto of created Transaction.
     */

    private TransactionDto completeTransaction(Transaction transaction, Set<Operation> operations) {
        transaction.setStatus(TransactionStatus.CREATED);

        try {
            transactionServiceUtil.changeAccountAmount(operations, transaction);
        } catch (ValidationException exception) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transactionRepository.save(transaction);
            throw new ValidationException("CREDIT amount is more than stored in this account.");
        }

        Transaction createdTransaction = transactionRepository.save(transaction);

        TransactionDto dtoOfCreateTransaction = transactionDtoMapper.toDto(createdTransaction);

        Set<OperationDto> operationDtos = operationDtoMapper.toDtos(operations);

        dtoOfCreateTransaction.setOperations(operationDtos);
        return dtoOfCreateTransaction;
    }

}
