package com.itech.service.account.impl;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Role;
import com.itech.model.enumeration.Status;
import com.itech.repository.AccountRepository;
import com.itech.repository.CreationRequestRepository;
import com.itech.repository.UserRepository;
import com.itech.service.account.AccountService;
import com.itech.service.mail.EmailService;
import com.itech.utils.IbanGenerator;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.account.AccountDtoMapper;
import com.itech.utils.mapper.request.RequestDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of AccountService interface. Provides us different methods of Service layer to work with Repository layer of Account objects.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Service
@Log4j2
@PropertySources({
        @PropertySource("classpath:properties/exception.properties"),
        @PropertySource("classpath:properties/mail.properties")
})
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountDtoMapper accountDtoMapper;
    private final IbanGenerator ibanGenerator;
    private final JsonEntitySerializer serializer;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;
    private final CreationRequestRepository creationRequestRepository;
    private final JsonEntitySerializer jsonEntitySerializer;
    private final RequestDtoMapper requestDtoMapper;
    private final EmailService emailService;

    @Value("${exception.account.not.found}")
    private String accountNotFoundExceptionText;

    @Value("${mail.approve.message}")
    private String approveMessage;

    @Value("${mail.reject.message}")
    private String rejectMessage;

    @Value("${expired.request.time}")
    private long timeToBeExpired;

    @Value("${exception.authenticated.user.not.found}")
    private String authenticatedUserNotFoundExceptionText;

    @Value("${exception.account.creation.request.with.id.not.found}")
    private String creationRequestWithIdNotFoundExceptionText;

    @Value("${exception.id.of.logged.user.not.equals.id.of.account}")
    private String idOfLoggedUserNotEqualsIdOfAccountExceptionText;

    @Value("${exception.account.creation.requests.not.found}")
    private String accountCreationRequestsNotFoundExceptionText;

    @Value("${exception.logged.user.not.found}")
    private String loggedUserNotFoundExceptionText;

    @Value("${exception.user.email.not.found}")
    private String userEmailNotFoundExceptionText;

    @Value("${mail.rejected.message.title}")
    private String rejectedMessageTitleText;

    @Override
    public List<AccountDto> findAllAccounts() {

        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(loggedUserNotFoundExceptionText));
        List<Account> accounts;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            accounts = accountRepository.findAccountsByUser(authenticatedUser);
        } else {
            accounts = accountRepository.findAll();
        }

        if (accounts.isEmpty()) {
            throw new EntityNotFoundException(accountNotFoundExceptionText);
        }

        return accounts.stream().map(accountDtoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AccountDto findAccountByAccountId(Long accountId) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(authenticatedUserNotFoundExceptionText));

        Optional<Account> foundAccount;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            foundAccount = accountRepository.findAccountByIdAndUser(accountId, authenticatedUser);
        } else {
            foundAccount = accountRepository.findAccountById(accountId);
        }

        return accountDtoMapper.toDto(foundAccount.orElseThrow(() -> new EntityNotFoundException(accountNotFoundExceptionText)));
    }

    @Override
    public Long createAccount(AccountCreateDto accountChangeDto) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(authenticatedUserNotFoundExceptionText));

        CreationRequest accountCreatingRequest = new CreationRequest();
        log.debug("CreationRequest empty object created.");

        accountCreatingRequest.setStatus(Status.IN_PROGRESS);
        log.debug("CreationRequest status set.");

        accountCreatingRequest.setCreationType(CreationType.ACCOUNT);
        log.debug("CreationRequest type set.");

        accountCreatingRequest.setPayload(serializer.serializeObjectToJson(accountChangeDto));
        log.debug("CreationRequest payload set.");

        accountCreatingRequest.setIssuedAt(LocalDateTime.now());
        log.debug("CreationRequest issuedAt time set.");

        accountCreatingRequest.setUser(authenticatedUser);
        log.debug("CreationRequest user set.");

        log.info("Account was created successfully!");
        return creationRequestRepository.save(accountCreatingRequest).getId();
    }

    @Override
    public AccountDto updateAccount(AccountUpdateDto accountUpdateDto, Long accountId) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(authenticatedUserNotFoundExceptionText));

        Account accountToUpdate = accountRepository.findAccountById(accountId).orElseThrow(() -> new EntityNotFoundException(accountNotFoundExceptionText));

        accountToUpdate.setAmount(accountUpdateDto.getAmount());

        if (authenticatedUser.getRole().equals(Role.USER) && !authenticatedUser.getId().equals(accountToUpdate.getUser().getId())) {
            throw new ValidationException(idOfLoggedUserNotEqualsIdOfAccountExceptionText);
        }

        return accountDtoMapper.toDto(accountRepository.save(accountToUpdate));
    }

    @Override
    public void deleteAccountByAccountId(Long accountId) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(authenticatedUserNotFoundExceptionText));

        Account foundAccountToDelete = accountRepository.findAccountById(accountId).orElseThrow(() -> new EntityNotFoundException(accountNotFoundExceptionText));

        if (authenticatedUser.getRole().equals(Role.USER) && !authenticatedUser.getId().equals(foundAccountToDelete.getId())) {
            throw new ValidationException(idOfLoggedUserNotEqualsIdOfAccountExceptionText);
        } else {
            accountRepository.deleteById(foundAccountToDelete.getId());
        }
    }

    @Override
    public CreationRequestDto findAccountCreationRequestById(Long creationRequestId) {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(authenticatedUserNotFoundExceptionText));

        if (authenticatedUser.getRole().equals(Role.USER)) {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.ACCOUNT, creationRequestId, authenticatedUser).orElseThrow(() -> new EntityNotFoundException(creationRequestWithIdNotFoundExceptionText)));
        } else {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.ACCOUNT, creationRequestId).orElseThrow(() -> new EntityNotFoundException(creationRequestWithIdNotFoundExceptionText)));
        }
    }

    @Override
    public List<CreationRequestDto> findAccountCreationRequests() {
        User authenticatedUser = userRepository.findUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException(authenticatedUserNotFoundExceptionText));

        List<CreationRequest> creationRequests;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.ACCOUNT, authenticatedUser);
        } else {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationType(CreationType.ACCOUNT);
        }

        if (creationRequests.isEmpty()) {
            throw new EntityNotFoundException(accountCreationRequestsNotFoundExceptionText);
        }

        return creationRequests.stream().map(requestDtoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void approveAccountCreationRequest(Long accountCreationRequestId) {
        CreationRequest accountCreationRequest = creationRequestRepository.findCreationRequestsByIdAndStatus(accountCreationRequestId, Status.IN_PROGRESS).orElseThrow(() -> new EntityNotFoundException(creationRequestWithIdNotFoundExceptionText));

        User accountCreationRequestUser = accountCreationRequest.getUser();

        AccountCreateDto accountChangeDtoFromCreationRequest = jsonEntitySerializer.serializeJsonToObject(accountCreationRequest.getPayload(), AccountCreateDto.class);

        Account accountToCreate = new Account();
        accountToCreate.setUser(accountCreationRequestUser);
        accountToCreate.setAmount(accountChangeDtoFromCreationRequest.getAmount());
        accountToCreate.setCurrency(accountChangeDtoFromCreationRequest.getCurrency());

        String accountNumber = ibanGenerator.generateIban(accountToCreate.getCurrency().getCountryCode());

        if (!accountRepository.findAccountByAccountNumber(accountNumber).isPresent()) {
            accountToCreate.setAccountNumber(accountNumber);
        } else {
            accountNumber = ibanGenerator.generateIban(accountToCreate.getCurrency().getCountryCode());
            accountToCreate.setAccountNumber(accountNumber);
        }

        accountRepository.save(accountToCreate);

        accountCreationRequest.setStatus(Status.CREATED);

        Long createdAccountId = accountRepository.findAccountByAccountNumber(accountToCreate.getAccountNumber()).orElseThrow(() -> new EntityNotFoundException(accountNotFoundExceptionText)).getId();
        accountCreationRequest.setCreatedId(createdAccountId);
        creationRequestRepository.save(accountCreationRequest);

        String userEmail = accountCreationRequestUser.getEmail();
        if (userEmail != null) {
            emailService.sendEmail(userEmail,
                    String.format("Request approved. Id of created account: %d", createdAccountId),
                    approveMessage);
        } else {
            throw new EntityNotFoundException(userEmailNotFoundExceptionText);
        }
    }

    public void rejectAccountCreationRequest(Long accountCreationRequestId) {
        CreationRequest accountCreationRequest = creationRequestRepository.findCreationRequestsByIdAndStatus(accountCreationRequestId, Status.IN_PROGRESS).orElseThrow(() -> new EntityNotFoundException(creationRequestWithIdNotFoundExceptionText));

        User accountCreationRequestUser = accountCreationRequest.getUser();

        accountCreationRequest.setStatus(Status.REJECTED);
        creationRequestRepository.save(accountCreationRequest);

        String userEmail = accountCreationRequestUser.getEmail();

        if (userEmail != null) {
            emailService.sendEmail(userEmail, rejectedMessageTitleText,
                    rejectMessage);
        } else {
            throw new EntityNotFoundException(userEmailNotFoundExceptionText);
        }
    }

    @Override
    public void checkExpiredAccountCreationRequests() {
        List<CreationRequest> accountCreationRequests = creationRequestRepository.findCreationRequestsByCreationTypeAndStatus(CreationType.ACCOUNT, Status.IN_PROGRESS);

        for (CreationRequest accountCreationRequest : accountCreationRequests) {
            LocalDateTime time = accountCreationRequest.getIssuedAt().plusSeconds(timeToBeExpired);
            if (time.isBefore(LocalDateTime.now())) {
                accountCreationRequest.setStatus(Status.EXPIRED);
                creationRequestRepository.save(accountCreationRequest);
                log.info(String.format("Expired account creation request id: %d", accountCreationRequest.getId()));
            }
        }
    }
}
