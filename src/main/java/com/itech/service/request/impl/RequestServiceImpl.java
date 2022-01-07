package com.itech.service.request.impl;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.Transaction;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Currency;
import com.itech.model.enumeration.Role;
import com.itech.model.enumeration.Status;
import com.itech.rabbit.RabbitMqPublisher;
import com.itech.repository.AccountRepository;
import com.itech.repository.CreationRequestRepository;
import com.itech.repository.UserRepository;
import com.itech.service.mail.EmailService;
import com.itech.service.request.RequestService;
import com.itech.utils.IbanGenerator;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.mapper.request.RequestDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of RequestService interface. Provides us different methods of Service layer to work with Repository layer of CreationRequest objects.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Service
public class RequestServiceImpl implements RequestService {
    @Value("${spring.mail.approvemessage}")
    private String approveMessage;

    @Value("${spring.mail.rejectmessage}")
    private String rejectMessage;

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

    @Autowired
    private JsonEntitySerializer jsonEntitySerializer;

    @Autowired
    private IbanGenerator ibanGenerator;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailService emailService;

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
     * findAccountCreationRequestById method. Finds CreationRequest with TRANSACTION CreationType by id and maps to Dto;
     *
     * @param creationRequestId Id of CreationRequest.
     * @return CreationRequestDto Dto of found CreationRequest object.
     */

    @Override
    public CreationRequestDto findTransactionCreationRequestById(Long creationRequestId) {
        return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.TRANSACTION, creationRequestId).orElseThrow(() -> new EntityNotFoundException("Transaction CreationRequest with this id not found!")));
    }

    /**
     * findAccountCreationRequestById method. Finds CreationRequest with ACCOUNT CreationType by id and maps to Dto;
     *
     * @param creationRequestId Id of CreationRequest.
     * @return CreationRequestDto Dto of found CreationRequest object.
     */

    @Override
    public CreationRequestDto findAccountCreationRequestById(Long creationRequestId) {
        return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.ACCOUNT, creationRequestId).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!")));
    }

    /**
     * findAccountCreationRequests method. Finds all CreationRequests with ACCOUNT CreationType and maps to Dto;
     *
     * @return List<CreationRequestDto> List of all CreationRequest objects.
     */

    @Override
    public List<CreationRequestDto> findAccountCreationRequests() {

        List<CreationRequest> creationRequests = creationRequestRepository.findCreationRequestsByCreationType(CreationType.ACCOUNT);
        if (creationRequests.isEmpty()) throw new EntityNotFoundException("Account CreationRequests not found!");

        List<CreationRequestDto> creationRequestDtos = new ArrayList<>();
        creationRequests.forEach(creationRequest -> creationRequestDtos.add(requestDtoMapper.toDto(creationRequest)));

        return creationRequestDtos;
    }

    /**
     * findTransactionCreationRequests method. Finds all CreationRequests with TRANSACTION CreationType and maps to Dto;
     *
     * @return List<CreationRequestDto> List of all CreationRequest objects.
     */

    @Override
    public List<CreationRequestDto> findTransactionCreationRequests() {

        List<CreationRequest> creationRequests = creationRequestRepository.findCreationRequestsByCreationType(CreationType.TRANSACTION);
        if (creationRequests.isEmpty()) throw new EntityNotFoundException("Transaction CreationRequests not found!");

        List<CreationRequestDto> creationRequestDtos = new ArrayList<>();
        creationRequests.forEach(creationRequest -> creationRequestDtos.add(requestDtoMapper.toDto(creationRequest)));

        return creationRequestDtos;
    }

    /**
     * approveAccountCreationRequest method. Approves CreationRequest and creates account based on payload of CreationRequest, sets CreationRequest status to CREATED, then send email message.
     *
     * @param accountCreationRequestId Id of CreationRequest we need to approve.
     */

    @Override
    public void approveAccountCreationRequest(Long accountCreationRequestId){
        CreationRequest accountCreationRequest = creationRequestRepository.findCreationRequestById(accountCreationRequestId).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!"));

        User accountCreationRequestUser = accountCreationRequest.getUser();

        AccountCreateDto accountCreateDtoFromCreationRequest = jsonEntitySerializer.serializeJsonToObject(accountCreationRequest.getPayload(), AccountCreateDto.class);

        Account accountToCreate = new Account();
        accountToCreate.setUser(accountCreationRequestUser);
        accountToCreate.setAmount(accountCreateDtoFromCreationRequest.getAmount());
        accountToCreate.setCurrency(Currency.valueOf(accountCreateDtoFromCreationRequest.getCurrency()));
        accountToCreate.setAccountNumber(ibanGenerator.generateIban(accountToCreate.getCurrency().getCountryCode()));
        accountRepository.save(accountToCreate);

        accountCreationRequest.setStatus(Status.CREATED);

        Long createdAccountId = accountRepository.findAccountByAccountNumber(accountToCreate.getAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account with this account number not found!")).getId();
        accountCreationRequest.setCreatedId(createdAccountId);
        creationRequestRepository.save(accountCreationRequest);

        emailService.sendEmail(accountCreationRequestUser.getEmail(),
                String.format("Request approved. Id of created account: %d", createdAccountId),
                approveMessage);
    }

    /**
     * rejectAccountCreationRequest method. Rejects CreationRequest, sets CreationRequest status to REJECTED, then send email message.
     *
     * @param accountCreationRequestId Id of CreationRequest we need to reject.
     */

    public void rejectAccountCreationRequest(Long accountCreationRequestId){
        CreationRequest accountCreationRequest = creationRequestRepository.findCreationRequestById(accountCreationRequestId).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!"));

        User accountCreationRequestUser = accountCreationRequest.getUser();

        accountCreationRequest.setStatus(Status.REJECTED);
        creationRequestRepository.save(accountCreationRequest);

        emailService.sendEmail(accountCreationRequestUser.getEmail(),
                "Request rejected.",
                rejectMessage);
    }
}
