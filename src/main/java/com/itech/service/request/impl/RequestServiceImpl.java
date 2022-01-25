package com.itech.service.request.impl;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Status;
import com.itech.rabbit.RabbitMqPublisher;
import com.itech.repository.CreationRequestRepository;
import com.itech.service.request.RequestService;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.literal.LogMessageText;
import com.itech.utils.mapper.request.RequestDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of RequestService interface. Provides us different methods of Service layer to work with Repository layer of CreationRequest objects.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class RequestServiceImpl implements RequestService {

    private final CreationRequestRepository creationRequestRepository;

    private final RabbitMqPublisher publisher;

    private final JwtDecoder jwtDecoder;

    private final JsonEntitySerializer serializer;

    private final RequestDtoMapper requestDtoMapper;

    private CreationRequest saveRequest(TransactionCreateDto transactionCreateDto) {
        User loggedUser = jwtDecoder.getLoggedUser();

        CreationRequest creationRequest = new CreationRequest();
        creationRequest.setUser(loggedUser);
        creationRequest.setCreationType(CreationType.TRANSACTION);
        creationRequest.setStatus(Status.IN_PROGRESS);
        creationRequest.setIssuedAt(LocalDateTime.now());
        creationRequest.setPayload(serializer.serializeObjectToJson(transactionCreateDto));

        return creationRequestRepository.save(creationRequest);
    }

    @Override
    public CreationRequestDto processCreationRequestMessage(TransactionCreateDto transactionCreateDto) {
        CreationRequestDto creationRequestDto = requestDtoMapper.toDto(saveRequest(transactionCreateDto));
        publisher.sendMessageToQueue(serializer.serializeObjectToJson(creationRequestDto));
        log.info(LogMessageText.MESSAGE_SENT_TO_QUEUE_LOG);
        return creationRequestDto;
    }
}
