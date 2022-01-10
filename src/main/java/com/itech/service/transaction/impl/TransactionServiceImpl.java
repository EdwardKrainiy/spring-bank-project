package com.itech.service.transaction.impl;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.*;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.OperationType;
import com.itech.model.enumeration.Role;
import com.itech.model.enumeration.Status;
import com.itech.repository.*;
import com.itech.service.transaction.TransactionService;
import com.itech.service.transaction.TransactionServiceUtil;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.request.RequestDtoMapper;
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
    private TransactionServiceUtil transactionServiceUtil;

    @Autowired
    private JsonEntitySerializer serializer;

    @Autowired
    private CreationRequestRepository creationRequestRepository;

    @Autowired
    private RequestDtoMapper requestDtoMapper;

    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * findTransactionById method. Finds transaction by transactionId.
     *
     * @param transactionId If of transaction we need to find.
     * @return TransactionDto Found transactionDto object.
     */

    @Override
    public TransactionDto findTransactionById(Long transactionId) {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!"));

        if (authenticatedUser.getRole().equals(Role.USER)) {
            return transactionDtoMapper.toDto(transactionRepository.getTransactionByIdAndUser(transactionId, authenticatedUser).orElseThrow(() -> new EntityNotFoundException("Transaction not found!")));
        } else {
            return transactionDtoMapper.toDto(transactionRepository.getTransactionById(transactionId).orElseThrow(() -> new EntityNotFoundException("Transaction not found!")));
        }
    }

    /**
     * findAllTransactions method. Finds all transactions, stored in DB.
     *
     * @return List<TransactionDto> List of all found transactionDto objects.
     */

    @Override
    public List<TransactionDto> findAllTransactions() {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!"));

        List<Transaction> transactions;

        List<TransactionDto> transactionDtos = new ArrayList<>();

        if (authenticatedUser.getRole().equals(Role.USER)) {
            transactions = transactionRepository.findTransactionsByUser(authenticatedUser);
        } else {
            transactions = transactionRepository.findAll();
        }

        if (transactions.isEmpty()) throw new EntityNotFoundException("Transaction not found!");

        transactions.forEach(transaction -> transactionDtos.add(transactionDtoMapper.toDto(transaction)));

        return transactionDtos;
    }

    /**
     * createTransaction method. Creates Transaction from transactionCreateDto.
     *
     * @param creationRequestDtoJson JSON of CreationRequestDto.
     * @return TransactionDto Obtained object of TransactionDto.
     */

    @Override
    public TransactionDto createTransaction(String creationRequestDtoJson) {
        CreationRequestDto creationRequestDto = serializer.serializeJsonToObject(creationRequestDtoJson, CreationRequestDto.class);

        TransactionCreateDto transactionCreateDto = serializer.serializeJsonToObject(creationRequestDto.getPayload(), TransactionCreateDto.class);

        CreationRequest requestToReject = creationRequestRepository.findCreationRequestById(creationRequestDto.getId()).orElseThrow(() -> new EntityNotFoundException("Creation Request not found!"));

        User foundUser = userRepository.getUserById(creationRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        LocalDateTime currentDate = LocalDateTime.now();
        Transaction transaction = new Transaction();

        transaction.setUser(foundUser);
        transaction.setIssuedAt(java.sql.Timestamp.valueOf(currentDate));
        transaction.setStatus(Status.IN_PROGRESS);

        transactionRepository.save(transaction);

        Set<Operation> operations = new LinkedHashSet<>();

        Set<OperationCreateDto> dtoOperations = transactionCreateDto.getOperations();

        for (OperationCreateDto operationCreateDto : dtoOperations) {
            Operation operation = new Operation();
            Account account = accountRepository.findAccountByAccountNumber(operationCreateDto.getAccountNumber()).orElse(null);
            if(account == null){
                requestToReject.setStatus(Status.REJECTED);
                requestToReject.setCreatedId(transaction.getId());
                creationRequestRepository.save(requestToReject);

                transaction.setStatus(Status.REJECTED);
                transactionRepository.save(transaction);
                throw new EntityNotFoundException("Account not found!");
            }
            operation.setAccount(account);
            operation.setTransaction(transaction);

            if (operationCreateDto.getOperationType().equals("DEBIT") || operationCreateDto.getOperationType().equals("CREDIT")) {
                operation.setOperationType(OperationType.valueOf(operationCreateDto.getOperationType()));
            } else {
                requestToReject.setStatus(Status.REJECTED);
                requestToReject.setCreatedId(transaction.getId());
                creationRequestRepository.save(requestToReject);

                transaction.setStatus(Status.REJECTED);
                transactionRepository.save(transaction);
                throw new ValidationException("Incorrect Operation Type!");
            }

            operation.setAmount(operationCreateDto.getAmount());
            operations.add(operationRepository.save(operation));
        }

        if (!transactionServiceUtil.checkRequestDtoValidity(operations, transaction)){
            requestToReject.setStatus(Status.REJECTED);
            requestToReject.setCreatedId(transaction.getId());
            creationRequestRepository.save(requestToReject);

            transaction.setStatus(Status.REJECTED);
            transactionRepository.save(transaction);

            throw new ValidationException("Incorrect structure of request. It must be at least 1 DEBIT and 1 CREDIT operations, and sum of CREDIT minus sum of DEBIT operation amounts must equals 0.");
        }

        operationRepository.saveAll(operations);

        transaction.setOperations(operations);

        return completeTransaction(transaction, operations, creationRequestDto);
    }

    /**
     * completeTransaction method. Provides us
     *
     * @param transaction Transaction object, which we need to write to DB.
     * @param operations  Set of operations we need to check request dto validity and to change account amount.
     * @return TransactionDto Dto of created Transaction.
     */

    private TransactionDto completeTransaction(Transaction transaction, Set<Operation> operations, CreationRequestDto creationRequestDto) {
        CreationRequest creationRequest = creationRequestRepository.findCreationRequestById(creationRequestDto.getId()).orElseThrow(() -> new EntityNotFoundException("CreationRequest not found!"));

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

        operationRepository.saveAll(transaction.getOperations());

        creationRequestRepository.save(creationRequest);
        Transaction createdTransaction = transactionRepository.save(transaction);

        log.info("Transaction was created successfully!");
        return transactionDtoMapper.toDto(createdTransaction);
    }

    /**
     * findAccountCreationRequestById method. Finds CreationRequest with TRANSACTION CreationType by id and maps to Dto;
     *
     * @param creationRequestId Id of CreationRequest.
     * @return CreationRequestDto Dto of found CreationRequest object.
     */

    @Override
    public CreationRequestDto findTransactionCreationRequestById(Long creationRequestId) {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!"));

        if (authenticatedUser.getRole().equals(Role.USER)) {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.TRANSACTION, creationRequestId, authenticatedUser).orElseThrow(() -> new EntityNotFoundException("Transaction CreationRequest with this id not found!")));
        } else {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.TRANSACTION, creationRequestId).orElseThrow(() -> new EntityNotFoundException("Transaction CreationRequest with this id not found!")));
        }
    }

    /**
     * findTransactionCreationRequests method. Finds all CreationRequests with TRANSACTION CreationType and maps to Dto;
     *
     * @return List<CreationRequestDto> List of all CreationRequest objects.
     */

    @Override
    public List<CreationRequestDto> findTransactionCreationRequests() {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!"));

        List<CreationRequest> creationRequests;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.TRANSACTION, authenticatedUser);
        } else {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationType(CreationType.TRANSACTION);
        }

        if (creationRequests.isEmpty()) throw new EntityNotFoundException("Transaction CreationRequests not found!");

        List<CreationRequestDto> creationRequestDtos = new ArrayList<>();
        creationRequests.forEach(creationRequest -> creationRequestDtos.add(requestDtoMapper.toDto(creationRequest)));

        return creationRequestDtos;
    }
}
