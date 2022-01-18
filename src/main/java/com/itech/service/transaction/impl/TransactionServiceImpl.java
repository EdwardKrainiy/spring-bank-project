package com.itech.service.transaction.impl;

import com.itech.model.dto.operation.OperationCreateDto;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.*;
import com.itech.model.enumeration.*;
import com.itech.repository.*;
import com.itech.service.transaction.TransactionService;
import com.itech.service.transaction.TransactionServiceUtil;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.ChangeAccountAmountException;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.message.ExceptionMessageText;
import com.itech.utils.mapper.request.RequestDtoMapper;
import com.itech.utils.mapper.transaction.TransactionDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionService interface. Provides us different methods of Service layer to work with Repository layer of Transaction objects.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    private final TransactionDtoMapper transactionDtoMapper;

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    private final OperationRepository operationRepository;

    private final TransactionServiceUtil transactionServiceUtil;

    private final JsonEntitySerializer serializer;

    private final CreationRequestRepository creationRequestRepository;

    private final RequestDtoMapper requestDtoMapper;

    private final JwtDecoder jwtDecoder;

    @Override
    public TransactionDto findTransactionById(Long transactionId) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.AUTHENTICATED_USER_NOT_FOUND));

        Optional<Transaction> foundTransaction;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            foundTransaction = transactionRepository.findTransactionByIdAndUser(transactionId, authenticatedUser);
        } else {
            foundTransaction = transactionRepository.findTransactionById(transactionId);
        }

        return transactionDtoMapper.toDto(foundTransaction.orElseThrow(() -> new EntityNotFoundException("Transaction not found!")));
    }

    @Override
    public List<TransactionDto> findAllTransactions() {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.AUTHENTICATED_USER_NOT_FOUND));

        List<Transaction> transactions;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            transactions = transactionRepository.findTransactionsByUser(authenticatedUser);
        } else {
            transactions = transactionRepository.findAll();
        }

        if (transactions.isEmpty()) throw new EntityNotFoundException(ExceptionMessageText.TRANSACTION_CREATION_REQUESTS_NOT_FOUND);

        return transactions.stream().map(transactionDtoMapper::toDto).collect(Collectors.toList());

    }

    @Override
    public TransactionDto createTransaction(String creationRequestDtoJson) {
        CreationRequestDto creationRequestDto = serializer.serializeJsonToObject(creationRequestDtoJson, CreationRequestDto.class);
        TransactionCreateDto transactionCreateDto = serializer.serializeJsonToObject(creationRequestDto.getPayload(), TransactionCreateDto.class);
        CreationRequest requestToReject = creationRequestRepository.findCreationRequestById(creationRequestDto.getId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.TRANSACTION_CREATION_REQUEST_WITH_ID_NOT_FOUND));
        User foundUser = userRepository.findById(creationRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.USER_NOT_FOUND));

        Transaction transaction = createAndSaveTransaction(foundUser);

        if (accountRepository.findAll().isEmpty()) {
            rejectCreationRequest(requestToReject, transaction, ExceptionMessageText.ACCOUNTS_ARE_EMPTY);
        }

        Set<Operation> operations = new LinkedHashSet<>();
        Set<OperationCreateDto> dtoOperations = transactionCreateDto.getOperations();

        Optional<OperationCreateDto> firstOperation = dtoOperations.stream().findFirst();

        Currency currencyToCheck = null;

        if (firstOperation.isPresent()) {
            Optional<Account> expectedAccount = accountRepository.findAccountByAccountNumber(firstOperation.get().getAccountNumber());

            if (expectedAccount.isPresent()) {
                currencyToCheck = expectedAccount.get().getCurrency();
            } else {
                rejectCreationRequest(requestToReject, transaction, ExceptionMessageText.ACCOUNT_NOT_FOUND);
            }
        } else {
            rejectCreationRequest(requestToReject, transaction, ExceptionMessageText.OPERATIONS_ARE_EMPTY);
        }

        validateAndAddOperationsToSet(dtoOperations, requestToReject, transaction, currencyToCheck, operations);

        if (!transactionServiceUtil.checkRequestDtoValidity(operations, transaction)) {
            rejectCreationRequest(requestToReject, transaction, ExceptionMessageText.INCORRECT_REQUEST_STRUCTURE);
        }

        operationRepository.saveAll(operations);

        transaction.setOperations(operations);

        return completeTransaction(transaction, operations, creationRequestDto);
    }

    private Transaction createAndSaveTransaction(User foundUser) {
        LocalDateTime currentDate = LocalDateTime.now();
        Transaction transaction = new Transaction();

        transaction.setUser(foundUser);
        transaction.setIssuedAt(currentDate);
        transaction.setStatus(Status.IN_PROGRESS);

        return transactionRepository.save(transaction);
    }

    private void validateAndAddOperationsToSet(Set<OperationCreateDto> dtoOperations, CreationRequest requestToReject, Transaction transaction, Currency currencyToCheck, Set<Operation> operations) {
        for (OperationCreateDto operationCreateDto : dtoOperations) {
            accountRepository.findAccountByAccountNumber(operationCreateDto.getAccountNumber()).ifPresentOrElse(foundAccount -> completeOperationAdding(foundAccount, currencyToCheck, requestToReject, transaction, operationCreateDto, operations), () -> rejectCreationRequest(requestToReject, transaction, ExceptionMessageText.ACCOUNT_NOT_FOUND));
        }
    }

    private void rejectCreationRequest(CreationRequest requestToReject, Transaction transaction, String exceptionMessage) {
        requestToReject.setStatus(Status.REJECTED);
        requestToReject.setCreatedId(transaction.getId());
        creationRequestRepository.save(requestToReject);

        transaction.setStatus(Status.REJECTED);
        transactionRepository.save(transaction);
        throw new EntityNotFoundException(exceptionMessage);
    }

    private void completeOperationAdding(Account account, Currency currencyToCheck, CreationRequest requestToReject, Transaction transaction, OperationCreateDto operationCreateDto, Set<Operation> operations) {
        Operation operation = new Operation();

        if (!currencyToCheck.equals(account.getCurrency())) {
            rejectCreationRequest(requestToReject, transaction, ExceptionMessageText.CURRENCIES_ARE_NOT_SAME);
        }

        operation.setAccount(account);
        operation.setTransaction(transaction);

        operation.setOperationType(OperationType.valueOf(operationCreateDto.getOperationType()));

        operation.setAmount(operationCreateDto.getAmount());
        operations.add(operationRepository.save(operation));
    }

    private TransactionDto completeTransaction(Transaction transaction, Set<Operation> operations, CreationRequestDto creationRequestDto) {
        CreationRequest creationRequest = creationRequestRepository.findCreationRequestById(creationRequestDto.getId()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.TRANSACTION_CREATION_REQUEST_WITH_ID_NOT_FOUND));

        creationRequest.setStatus(Status.CREATED);
        transaction.setStatus(Status.CREATED);
        creationRequest.setCreatedId(transaction.getId());

        try {
            transactionServiceUtil.changeAccountAmount(operations);
        } catch (ChangeAccountAmountException exception) {
            rejectCreationRequest(creationRequest, transaction, ExceptionMessageText.CREDIT_IS_MORE_THAN_STORED_ON_ACCOUNT);
        }

        operationRepository.saveAll(transaction.getOperations());

        creationRequestRepository.save(creationRequest);
        Transaction createdTransaction = transactionRepository.save(transaction);

        log.info("Transaction was created successfully!");
        return transactionDtoMapper.toDto(createdTransaction);
    }

    @Override
    public CreationRequestDto findTransactionCreationRequestById(Long creationRequestId) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.AUTHENTICATED_USER_NOT_FOUND));

        if (authenticatedUser.getRole().equals(Role.USER)) {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.TRANSACTION, creationRequestId, authenticatedUser).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.TRANSACTION_CREATION_REQUEST_WITH_ID_NOT_FOUND)));
        } else {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.TRANSACTION, creationRequestId).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.TRANSACTION_CREATION_REQUEST_WITH_ID_NOT_FOUND)));
        }
    }

    @Override
    public List<CreationRequestDto> findTransactionCreationRequests() {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.AUTHENTICATED_USER_NOT_FOUND));

        List<CreationRequest> creationRequests;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.TRANSACTION, authenticatedUser);
        } else {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationType(CreationType.TRANSACTION);
        }

        if (creationRequests.isEmpty()) throw new EntityNotFoundException(ExceptionMessageText.TRANSACTION_CREATION_REQUESTS_NOT_FOUND);

        return creationRequests.stream().map(requestDtoMapper::toDto).collect(Collectors.toList());
    }
}
