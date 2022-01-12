package com.itech.service.request;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;

/**
 * RequestService interface. Provides us different methods to work with CreationRequestDto objects on Service layer.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
public interface RequestService {
    /**
     * processCreationRequestMessage method. Processes message, sends to RabbitMQ queue and saves to DB.
     *
     * @param transactionCreateDto Object, which contains all info we need to create Transaction.
     * @return CreationRequestDto Dto of creationRequest, which was sent to queue.
     */
    CreationRequestDto processCreationRequestMessage(TransactionCreateDto transactionCreateDto);
}
