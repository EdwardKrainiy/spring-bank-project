package com.itech.service.account.impl;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Currency;
import com.itech.model.enumeration.Role;
import com.itech.model.enumeration.Status;
import com.itech.repository.AccountRepository;
import com.itech.repository.CreationRequestRepository;
import com.itech.service.account.AccountService;
import com.itech.service.mail.EmailService;
import com.itech.utils.IbanGenerator;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.literal.ExceptionMessage;
import com.itech.utils.literal.LogMessage;
import com.itech.utils.literal.PropertySourceClasspath;
import com.itech.utils.mapper.account.AccountDtoMapper;
import com.itech.utils.mapper.request.RequestDtoMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Implementation of AccountService interface. Provides us different methods of Service layer to
 * work with Repository layer of Account objects.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
@Service
@Log4j2
@PropertySource(PropertySourceClasspath.MAIL_PROPERTIES_CLASSPATH)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final AccountDtoMapper accountDtoMapper;
  private final IbanGenerator ibanGenerator;
  private final JsonEntitySerializer serializer;
  private final JwtDecoder jwtDecoder;
  private final CreationRequestRepository creationRequestRepository;
  private final JsonEntitySerializer jsonEntitySerializer;
  private final RequestDtoMapper requestDtoMapper;
  private final EmailService emailService;

  @Value("${mail.approve.message}")
  private String approveMessage;

  @Value("${mail.reject.message}")
  private String rejectMessage;

  @Value("${expired.request.time}")
  private long timeToBeExpired;

  @Value("${mail.rejected.message.title}")
  private String rejectedMessageTitleText;

  @Override
  public List<AccountDto> findAllAccounts() {

    User authenticatedUser = jwtDecoder.getLoggedUser();
    List<Account> accounts;

    if (authenticatedUser.getRole().equals(Role.USER)) {
      accounts = accountRepository.findAccountsByUser(authenticatedUser);
    } else {
      accounts = accountRepository.findAll();
    }

    return accounts.stream().map(accountDtoMapper::toDto).collect(Collectors.toList());
  }

  @Override
  public AccountDto findAccountByAccountId(Long accountId) {
    User authenticatedUser = jwtDecoder.getLoggedUser();
    Optional<Account> foundAccount;

    if (authenticatedUser.getRole().equals(Role.USER)) {
      foundAccount = accountRepository.findAccountByIdAndUser(accountId, authenticatedUser);
    } else {
      foundAccount = accountRepository.findAccountById(accountId);
    }

    if (foundAccount.isPresent()) {
      return accountDtoMapper.toDto(foundAccount.get());
    } else {
      log.error(String.format(LogMessage.ACCOUNT_NOT_FOUND_LOG, accountId));
      throw new EntityNotFoundException(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }
  }

  @Override
  public Long createAccount(AccountCreateDto accountChangeDto) {
    User authenticatedUser = jwtDecoder.getLoggedUser();

    CreationRequest accountCreatingRequest = new CreationRequest();

    accountCreatingRequest.setStatus(Status.IN_PROGRESS);
    accountCreatingRequest.setCreationType(CreationType.ACCOUNT);
    accountCreatingRequest.setPayload(serializer.serializeObjectToJson(accountChangeDto));
    accountCreatingRequest.setIssuedAt(LocalDateTime.now());
    accountCreatingRequest.setUser(authenticatedUser);

    Long createdAccountCreationRequestId =
        creationRequestRepository.save(accountCreatingRequest).getId();
    log.info(
        String.format(
            LogMessage.ACCOUNT_CREATION_REQUEST_CREATED_LOG, createdAccountCreationRequestId));
    return createdAccountCreationRequestId;
  }

  @Override
  public AccountDto updateAccount(AccountUpdateDto accountUpdateDto, Long accountId) {
    User authenticatedUser = jwtDecoder.getLoggedUser();

    Optional<Account> accountToUpdateOptional = accountRepository.findAccountById(accountId);
    Account accountToUpdate;
    if (!accountToUpdateOptional.isPresent()) {
      log.error(String.format(LogMessage.ACCOUNT_NOT_FOUND_LOG, accountId));
      throw new EntityNotFoundException(ExceptionMessage.ACCOUNT_NOT_FOUND);
    } else {
      accountToUpdate = accountToUpdateOptional.get();
      accountToUpdate.setAmount(accountUpdateDto.getAmount());

      if (authenticatedUser.getRole().equals(Role.USER)
          && !authenticatedUser.getId().equals(accountToUpdate.getUser().getId())) {
        log.error(
            String.format(
                LogMessage.ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT_LOG,
                authenticatedUser.getId(),
                accountToUpdate.getUser().getId()));
        throw new ValidationException(
            ExceptionMessage.ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT);
      } else {
        log.info(String.format(LogMessage.ACCOUNT_UPDATED_LOG, accountId));
        return accountDtoMapper.toDto(accountRepository.save(accountToUpdate));
      }
    }
  }

  @Override
  public void deleteAccountByAccountId(Long accountId) {
    User authenticatedUser = jwtDecoder.getLoggedUser();
    Optional<Account> accountToDeleteOptional = accountRepository.findAccountById(accountId);

    if (!accountToDeleteOptional.isPresent()) {
      log.error(String.format(LogMessage.ACCOUNT_NOT_FOUND_LOG, accountId));
      throw new EntityNotFoundException(ExceptionMessage.ACCOUNT_NOT_FOUND);
    } else {
      Account accountToDelete = accountToDeleteOptional.get();
      if (authenticatedUser.getRole().equals(Role.USER)
          && !authenticatedUser.getId().equals(accountToDelete.getId())) {
        log.error(
            String.format(
                LogMessage.ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT_LOG,
                authenticatedUser.getId(),
                accountToDelete.getUser().getId()));
        throw new ValidationException(
            ExceptionMessage.ID_OF_LOGGED_USER_NOT_EQUALS_ID_OF_ACCOUNT);
      } else {
        log.info(String.format(LogMessage.ACCOUNT_DELETED_LOG, accountId));
        accountRepository.deleteById(accountToDelete.getId());
      }
    }
  }

  @Override
  public CreationRequestDto findAccountCreationRequestById(Long creationRequestId) {
    User authenticatedUser = jwtDecoder.getLoggedUser();
    Optional<CreationRequest> foundCreationRequestOptional;

    if (authenticatedUser.getRole().equals(Role.USER)) {
      foundCreationRequestOptional =
          creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(
              CreationType.ACCOUNT, creationRequestId, authenticatedUser);
    } else {
      foundCreationRequestOptional =
          creationRequestRepository.findCreationRequestsByCreationTypeAndId(
              CreationType.ACCOUNT, creationRequestId);
    }
    if (!foundCreationRequestOptional.isPresent()) {
      log.error(
          String.format(LogMessage.ACCOUNT_CREATION_REQUEST_NOT_FOUND_LOG, creationRequestId));
      throw new EntityNotFoundException(ExceptionMessage.ACCOUNT_CREATION_REQUEST_NOT_FOUND);
    } else {
      return requestDtoMapper.toDto(foundCreationRequestOptional.get());
    }
  }

  @Override
  public List<CreationRequestDto> findAccountCreationRequests() {
    User authenticatedUser = jwtDecoder.getLoggedUser();

    List<CreationRequest> creationRequests;

    if (authenticatedUser.getRole().equals(Role.USER)) {
      creationRequests =
          creationRequestRepository.findCreationRequestsByCreationTypeAndUser(
              CreationType.ACCOUNT, authenticatedUser);
    } else {
      creationRequests =
          creationRequestRepository.findCreationRequestsByCreationType(CreationType.ACCOUNT);
    }

    return creationRequests.stream().map(requestDtoMapper::toDto).collect(Collectors.toList());
  }

  @Override
  public void approveAccountCreationRequest(Long accountCreationRequestId) {
    CreationRequest accountCreationRequest = obtainAccountCreationRequest(accountCreationRequestId);

    User accountCreationRequestUser = accountCreationRequest.getUser();

    AccountCreateDto accountChangeDtoFromCreationRequest =
        jsonEntitySerializer.serializeJsonToObject(
            accountCreationRequest.getPayload(), AccountCreateDto.class);

    Account accountToCreate = new Account();
    accountToCreate.setUser(accountCreationRequestUser);
    accountToCreate.setAmount(accountChangeDtoFromCreationRequest.getAmount());
    accountToCreate.setCurrency(
        Currency.valueOf(
            accountChangeDtoFromCreationRequest.getCurrency().toUpperCase(Locale.ROOT)));

    String accountNumber =
        ibanGenerator.generateIban(accountToCreate.getCurrency().getCountryCode());

    while (accountRepository.findAccountByAccountNumber(accountNumber).isPresent()) {
      accountNumber = ibanGenerator.generateIban(accountToCreate.getCurrency().getCountryCode());
    }

    accountToCreate.setAccountNumber(accountNumber);

    accountRepository.save(accountToCreate);
    Long createdAccountId;
    Optional<Account> createdAccountOptional =
        accountRepository.findAccountByAccountNumber(accountNumber);
    if (!createdAccountOptional.isPresent()) {
      log.error(String.format(LogMessage.ACCOUNT_WITH_NUMBER_NOT_FOUND_LOG, accountNumber));
      throw new EntityNotFoundException(ExceptionMessage.ACCOUNT_NOT_FOUND);
    } else {
      createdAccountId = createdAccountOptional.get().getId();
    }

    log.info(String.format(LogMessage.ACCOUNT_CREATED_LOG, createdAccountId));

    accountCreationRequest.setStatus(Status.CREATED);

    accountCreationRequest.setCreatedId(createdAccountId);
    creationRequestRepository.save(accountCreationRequest);
    log.info(
        String.format(
            LogMessage.ACCOUNT_CREATION_REQUEST_UPDATED_LOG, accountCreationRequest.getId()));

    String userEmail = accountCreationRequestUser.getEmail();
    if (userEmail != null) {
      emailService.sendEmail(
          userEmail,
          String.format("Request approved. Id of created account: %d", createdAccountId),
          approveMessage);
      log.info(String.format(LogMessage.MESSAGE_SENT_LOG, userEmail));
    } else {
      log.warn(
          String.format(LogMessage.EMAIL_NOT_FOUND_LOG, accountCreationRequestUser.getId()));
    }
  }

  public void rejectAccountCreationRequest(Long accountCreationRequestId) {
    CreationRequest accountCreationRequest = obtainAccountCreationRequest(accountCreationRequestId);
    User accountCreationRequestUser = accountCreationRequest.getUser();

    accountCreationRequest.setStatus(Status.REJECTED);
    creationRequestRepository.save(accountCreationRequest);
    log.info(
        String.format(
            LogMessage.ACCOUNT_CREATION_REQUEST_UPDATED_LOG, accountCreationRequestId));

    String userEmail = accountCreationRequestUser.getEmail();

    if (userEmail != null) {
      emailService.sendEmail(userEmail, rejectedMessageTitleText, rejectMessage);
      log.info(String.format(LogMessage.MESSAGE_SENT_LOG, userEmail));
    } else {
      log.warn(
          String.format(LogMessage.EMAIL_NOT_FOUND_LOG, accountCreationRequestUser.getId()));
    }
  }

  @Override
  public void checkExpiredAccountCreationRequests() {
    List<CreationRequest> accountCreationRequests =
        creationRequestRepository.findCreationRequestsByCreationTypeAndStatus(
            CreationType.ACCOUNT, Status.IN_PROGRESS);

    for (CreationRequest accountCreationRequest : accountCreationRequests) {
      LocalDateTime time = accountCreationRequest.getIssuedAt().plusSeconds(timeToBeExpired);
      if (time.isBefore(LocalDateTime.now())) {
        accountCreationRequest.setStatus(Status.EXPIRED);
        creationRequestRepository.save(accountCreationRequest);
        log.info(
            String.format(
                "Expired account creation request id: %d", accountCreationRequest.getId()));
      }
    }
  }

  private CreationRequest obtainAccountCreationRequest(Long accountCreationRequestId) {
    Optional<CreationRequest> accountCreationRequestOptional =
        creationRequestRepository.findCreationRequestsByIdAndStatusAndCreationType(
            accountCreationRequestId, Status.IN_PROGRESS, CreationType.ACCOUNT);
    if (!accountCreationRequestOptional.isPresent()) {
      log.error(
          String.format(
              LogMessage.ACCOUNT_CREATION_REQUEST_NOT_FOUND_LOG, accountCreationRequestId));
      throw new EntityNotFoundException(ExceptionMessage.ACCOUNT_CREATION_REQUEST_NOT_FOUND);
    } else {
      return accountCreationRequestOptional.get();
    }
  }
}
