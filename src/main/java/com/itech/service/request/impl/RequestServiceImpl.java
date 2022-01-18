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
import com.itech.service.user.UserService;
import com.itech.service.user.impl.UserServiceImpl;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.exception.message.ExceptionMessageText;
import com.itech.utils.mapper.request.RequestDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of RequestService interface. Provides us different methods of Service layer to work with Repository layer of CreationRequest objects.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final CreationRequestRepository creationRequestRepository;

    private final UserService userService;

    private final RabbitMqPublisher publisher;

    private final UserRepository userRepository;

    private final JwtDecoder jwtDecoder;

    private final JsonEntitySerializer serializer;

    private final RequestDtoMapper requestDtoMapper;

    private CreationRequest saveRequest(TransactionCreateDto transactionCreateDto) {
        User foundUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (!userService.isUserActivated(foundUser)) throw new ValidationException(ExceptionMessageText.USER_NOT_ACTIVATED);

        CreationRequest creationRequest = new CreationRequest();
        creationRequest.setUser(foundUser);
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
        return creationRequestDto;
    }


}
