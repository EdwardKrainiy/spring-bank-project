package com.itech.service.request.impl;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Status;
import com.itech.rabbit.RabbitMqPublisher;
import com.itech.repository.CreationRequestRepository;
import com.itech.repository.UserRepository;
import com.itech.service.request.RequestService;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.mapper.request.RequestDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of RequestService interface. Provides us different methods of Service layer to work with Repository layer of CreationRequest objects.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Service
public class RequestServiceImpl implements RequestService {
    @Autowired
    private CreationRequestRepository creationRequestRepository;

    @Autowired
    private RabbitMqPublisher publisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JsonEntitySerializer serializer;

    @Autowired
    private RequestDtoMapper requestDtoMapper;

    private CreationRequest saveRequest(TransactionCreateDto transactionCreateDto) {
        User foundUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        CreationRequest creationRequest = new CreationRequest();
        creationRequest.setUser(foundUser);
        creationRequest.setCreationType(CreationType.TRANSACTION);
        creationRequest.setStatus(Status.IN_PROGRESS);
        creationRequest.setPayload(serializer.serializeObjectToJson(transactionCreateDto));

        return creationRequestRepository.save(creationRequest);
    }

    /**
     * processCreationRequestMessage method. Saves transactionCreateDto to DB, maps this one to CreationRequestDto object and sends JSON of this object to queue.
     *
     * @param transactionCreateDto Object of transactionCreateDto we need to store to DB and map to CreationRequestDto.
     * @return CreationRequestDto object of CreationRequestDto.
     */

    @Override
    public CreationRequestDto processCreationRequestMessage(TransactionCreateDto transactionCreateDto) {
        CreationRequestDto creationRequestDto = requestDtoMapper.toDto(saveRequest(transactionCreateDto));
        publisher.sendMessageToQueue(serializer.serializeObjectToJson(creationRequestDto));
        return creationRequestDto;
    }

    /**
     * findCreationRequestById method. Finds CreationRequest by id and maps to Dto;
     *
     * @param creationRequestId Id of CreationRequest.
     * @return CreationRequestDto Dto of found CreationRequest object.
     */

    @Override
    public CreationRequestDto findCreationRequestById(Long creationRequestId) {
        return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestById(creationRequestId).orElseThrow(() -> new EntityNotFoundException("CreationRequest not found!")));
    }
}
