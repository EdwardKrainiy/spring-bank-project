package com.itech.service.transaction;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionDto;

import java.util.List;

/**
 * TransactionService interface. Provides us different methods to work with Transaction objects on Service layer.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
public interface TransactionService {

    /**
     * findTransactionById method. Finds transaction by transactionId.
     *
     * @param transactionId If of transaction we need to find.
     * @return TransactionDto Found transactionDto object.
     */

    TransactionDto findTransactionById(Long transactionId);

    /**
     * findAllTransactions method. Finds all transactions, stored in DB.
     *
     * @return List<TransactionDto> List of all found transactionDto objects.
     */

    List<TransactionDto> findAllTransactions();

    /**
     * createTransaction method. Creates Transaction from transactionCreateDto.
     *
     * @param creationRequestJson JSON of CreationRequestDto.
     * @return TransactionDto Obtained object of TransactionDto.
     */

    TransactionDto createTransaction(String creationRequestJson);

    /**
     * findAccountCreationRequestById method. Finds CreationRequest with TRANSACTION CreationType by id and maps to Dto;
     *
     * @param creationRequestId Id of CreationRequest.
     * @return CreationRequestDto Dto of found CreationRequest object.
     */

    CreationRequestDto findTransactionCreationRequestById(Long creationRequestId);

    /**
     * findTransactionCreationRequests method. Finds all CreationRequests with TRANSACTION CreationType and maps to Dto;
     *
     * @return List<CreationRequestDto> List of all CreationRequest objects.
     */

    List<CreationRequestDto> findTransactionCreationRequests();
}
