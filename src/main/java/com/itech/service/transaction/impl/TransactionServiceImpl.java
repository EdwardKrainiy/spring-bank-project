package com.itech.service.transaction.impl;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.*;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.OperationType;
import com.itech.model.enumeration.Status;
import com.itech.repository.*;
import com.itech.service.transaction.TransactionService;
import com.itech.service.transaction.TransactionServiceUtil;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.transaction.TransactionDtoMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
@Log4j2
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

    @Autowired
    private TransactionServiceUtil transactionServiceUtil;

    @Autowired
    private JsonEntitySerializer serializer;

    @Autowired
    private CreationRequestRepository creationRequestRepository;


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

        CreationRequest creationRequest = new CreationRequest();
        creationRequest.setUser(foundUser);
        creationRequest.setCreationType(CreationType.TRANSACTION);
        creationRequest.setStatus(Status.IN_PROGRESS);
        creationRequest.setPayload(serializer.serializeObjectToJson(transactionCreateDto));
        creationRequestRepository.save(creationRequest);

        LocalDateTime currentDate = LocalDateTime.now();
        Transaction transaction = new Transaction();

        transaction.setUser(foundUser);
        transaction.setIssuedAt(java.sql.Timestamp.valueOf(currentDate));
        transaction.setStatus(Status.IN_PROGRESS);

        Set<Operation> operations = new LinkedHashSet<>();

        Set<OperationCreateDto> dtoOperations = transactionCreateDto.getOperations();

        for (OperationCreateDto operationCreateDto : dtoOperations) {
            Operation operation = new Operation();
            operation.setAccount(accountRepository.findAccountByAccountNumber(operationCreateDto.getAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account not found!")));
            operation.setTransaction(transaction);

            if (operationCreateDto.getOperationType().equals("DEBIT") || operationCreateDto.getOperationType().equals("CREDIT")) {
                operation.setOperationType(OperationType.valueOf(operationCreateDto.getOperationType()));

            } else throw new ValidationException("Incorrect Operation Type!");

            operation.setAmount(operationCreateDto.getAmount());
            operations.add(operation);
        }

        if (!transactionServiceUtil.checkRequestDtoValidity(operations, transaction))
            throw new ValidationException("Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.");


        operationRepository.saveAll(operations);

        transaction.setOperations(operations);

        return completeTransaction(transaction, operations, creationRequest);
    }

    /**
     * completeTransaction method. Provides us
     *
     * @param transaction Transaction object, which we need to write to DB.
     * @param operations  Set of operations we need to check request dto validity and to change account amount.
     * @return TransactionDto Dto of created Transaction.
     */

    private TransactionDto completeTransaction(Transaction transaction, Set<Operation> operations, CreationRequest creationRequest) {
        creationRequest.setStatus(Status.CREATED);
        transaction.setStatus(Status.CREATED);
        creationRequest.setCreatedId(transaction.getId());

        try {
            transactionServiceUtil.changeAccountAmount(operations);
        } catch (ValidationException exception) {
            creationRequest.setStatus(Status.REJECTED);
            transaction.setStatus(Status.REJECTED);

            creationRequestRepository.save(creationRequest);
            transactionRepository.save(transaction);

            throw new ValidationException("CREDIT amount is more than stored in this account.");
        }

        creationRequestRepository.save(creationRequest);
        Transaction createdTransaction = transactionRepository.save(transaction);

        log.info("Transaction was created successfully!");
        return transactionDtoMapper.toDto(createdTransaction);
    }

}
