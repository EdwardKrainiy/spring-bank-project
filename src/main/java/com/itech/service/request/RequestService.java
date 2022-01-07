package com.itech.service.request;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;

import java.util.List;

/**
 * RequestService interface. Provides us different methods to work with CreationRequestDto objects on Service layer.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
public interface RequestService {
    CreationRequestDto processCreationRequestMessage(TransactionCreateDto transactionCreateDto);

    CreationRequestDto findTransactionCreationRequestById(Long creationRequestId);

    CreationRequestDto findAccountCreationRequestById(Long creationRequestId);

    List<CreationRequestDto> findAccountCreationRequests();

    List<CreationRequestDto> findTransactionCreationRequests();

    void approveAccountCreationRequest(Long accountCreationRequestId);
    void rejectAccountCreationRequest(Long accountCreationRequestId);
}
