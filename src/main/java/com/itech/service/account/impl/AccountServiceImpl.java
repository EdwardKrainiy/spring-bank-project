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
import com.itech.repository.UserRepository;
import com.itech.service.account.AccountService;
import com.itech.service.mail.EmailService;
import com.itech.utils.IbanGenerator;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.account.AccountDtoMapper;
import com.itech.utils.mapper.account.AccountUpdateDtoMapper;
import com.itech.utils.mapper.request.RequestDtoMapper;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AccountService interface. Provides us different methods of Service layer to work with Repository layer of Account objects.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Value("${spring.mail.approvemessage}")
    private String approveMessage;

    @Value("${spring.mail.rejectmessage}")
    private String rejectMessage;

    @Value("${spring.time.expired.request.time}")
    private long timeToBeExpired;

    @Autowired
    private AccountRepository accountRepository; // TODO: use constructor injection

    @Autowired
    private AccountDtoMapper accountDtoMapper;

    @Autowired
    private AccountUpdateDtoMapper accountUpdateDtoMapper;

    @Autowired
    private IbanGenerator ibanGenerator;

    @Autowired
    private JsonEntitySerializer serializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private CreationRequestRepository creationRequestRepository;

    @Autowired
    private JsonEntitySerializer jsonEntitySerializer;

    @Autowired
    private RequestDtoMapper requestDtoMapper;

    @Autowired
    private EmailService emailService;

    /**
     * findAllAccounts method. Finds all accounts from DB.
     *
     * @return ResponseEntity<List < AccountDto>> ResponseEntity with HTTP code and list of all found accounts.
     */

    @Override
    public List<AccountDto> findAllAccounts() {

        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Logged user not found!")); //TODO: move literal to constants
        List<Account> accounts;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            accounts = accountRepository.findAccountsByUser(authenticatedUser);
        } else {
            accounts = accountRepository.findAll();
        }

        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("Account not found!"); // TODO: Account not found literal duplication 3 times. Move it to constant.
        }

        List<AccountDto> accountDtos = new ArrayList<>();
        accounts.forEach(account -> accountDtos.add(accountDtoMapper.toDto(account))); //TODO: better to use the following  List<AccountDto> collect = accounts.stream().map(accountDtoMapper::toDto).collect(Collectors.toList());

        return accountDtos;
    }

    /**
     * findAccountByAccountId. Finds account by id.
     *
     * @param accountId Id of account we need to find.
     * @return ResponseEntity<AccountDto> ResponseEntity with HTTP code and found account entity.
     */

    @Override
    public AccountDto findAccountByAccountId(Long accountId) {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!")); // TODO: henticated user not found! literal duplication 6 times. Move it to constant.

        Optional<Account> foundAccount;

        if (authenticatedUser.getRole().equals(Role.USER)) {
            foundAccount = accountRepository.findAccountByIdAndUser(accountId, authenticatedUser);
        } else {
            foundAccount = accountRepository.findAccountById(accountId);
        }

        return accountDtoMapper.toDto(foundAccount.orElseThrow(() -> new EntityNotFoundException("Account not found!"))); //TODO: move literal to constant
    }

    /**
     * createAccount method. Creates account from JSON object in RequestBody and saves into DB.
     *
     * @param accountCreateDto Account transfer object, which we need to save. This one will be converted into Account object, passed some checks and will be saved on DB.
     * @return ResponseEntity<Long> ResponseEntity with HTTP code and id of created account.
     */

    @Override
    public Long createAccount(AccountCreateDto accountCreateDto) {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!"));
        //TODO: add debug log here with full accounCreateDto content
        CreationRequest accountCreatingRequest = new CreationRequest();
        accountCreatingRequest.setStatus(Status.IN_PROGRESS);
        accountCreatingRequest.setCreationType(CreationType.ACCOUNT);
        accountCreatingRequest.setPayload(serializer.serializeObjectToJson(accountCreateDto));
        accountCreatingRequest.setIssuedAt(LocalDateTime.now());
        accountCreatingRequest.setUser(authenticatedUser);

        log.info("Account was created successfully!");
        return creationRequestRepository.save(accountCreatingRequest).getId();
    }

    /**
     * updateAccount method. Updates account by id and accountUpdateDto entity.
     *
     * @param accountUpdateDto Account transfer object, which we need to update. This one will be converted into Account object, passed some checks and will be updated on DB.
     * @param accountId        Id of account we need to update.
     */

    @Override
    public void updateAccount(AccountUpdateDto accountUpdateDto, Long accountId) { //TODO: should return updated account dto
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!")); //TODO: please move all literals to constants

        Account updateAccount = accountUpdateDtoMapper.toEntity(accountUpdateDto);

        updateAccount.setId(accountId);
        updateAccount.setAccountNumber(ibanGenerator.generateIban(accountUpdateDto.getCurrency().getCountryCode())); //TODO" why did you generate new account number per each update?

        if (authenticatedUser.getRole().equals(Role.USER) && !authenticatedUser.getId().equals(updateAccount.getUser().getId())) {
            throw new ValidationException("Id of this account is not equals id of logged user."); //TODO: please move all literals to constants
        }

        accountRepository.save(updateAccount);
    }

    /**
     * deleteAccountByAccountId method. Deletes account by id.
     *
     * @param accountId Id of account we need to delete.
     */

    @Override
    public void deleteAccountByAccountId(Long accountId) {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!")); //TODO: please move all literals to constants

        Account foundAccountToDelete = accountRepository.findAccountById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found!")); //TODO: please move all literals to constants

        if (authenticatedUser.getRole().equals(Role.USER) && !authenticatedUser.getId().equals(foundAccountToDelete.getId())) {
            throw new ValidationException("Id of this account is not equals id of logged user."); //TODO: please move all literals to constants
        } else {
            accountRepository.deleteAccountById(foundAccountToDelete.getId());
        }
    }

    /**
     * findAccountCreationRequestById method. Finds CreationRequest with ACCOUNT CreationType by id and maps to Dto;
     *
     * @param creationRequestId Id of CreationRequest.
     * @return CreationRequestDto Dto of found CreationRequest object.
     */

    @Override
    public CreationRequestDto findAccountCreationRequestById(Long creationRequestId) {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!")); //TODO: move all literals to constant

        if (authenticatedUser.getRole().equals(Role.USER)) {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndIdAndUser(CreationType.ACCOUNT, creationRequestId, authenticatedUser).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!"))); // TODO: remove literals duplication. Move to constant.
        } else {
            return requestDtoMapper.toDto(creationRequestRepository.findCreationRequestsByCreationTypeAndId(CreationType.ACCOUNT, creationRequestId).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!")));
        }
    }

    /**
     * findAccountCreationRequests method. Finds all CreationRequests with ACCOUNT CreationType and maps to Dto;
     *
     * @return List<CreationRequestDto> List of all CreationRequest objects.
     */

    @Override
    public List<CreationRequestDto> findAccountCreationRequests() {
        User authenticatedUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found!")); //TODO: to constant

        List<CreationRequest> creationRequests;

        List<CreationRequestDto> creationRequestDtos = new ArrayList<>();

        if (authenticatedUser.getRole().equals(Role.USER)) {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationTypeAndUser(CreationType.ACCOUNT, authenticatedUser);
        } else {
            creationRequests = creationRequestRepository.findCreationRequestsByCreationType(CreationType.ACCOUNT);
        }

        if (creationRequests.isEmpty()) {
            throw new EntityNotFoundException("Account CreationRequests not found!");  ////TODO: to constant
        }

        creationRequests.forEach(creationRequest -> creationRequestDtos.add(requestDtoMapper.toDto(creationRequest))); // TODO: use streamApi functionality for convertion. creationRequests.stream.map ....

        return creationRequestDtos;
    }

    /**
     * approveAccountCreationRequest method. Approves CreationRequest and creates account based on payload of CreationRequest, sets CreationRequest status to CREATED, then send email message.
     *
     * @param accountCreationRequestId Id of CreationRequest we need to approve.
     */

    @Override
    public void approveAccountCreationRequest(Long accountCreationRequestId) {
        CreationRequest accountCreationRequest = creationRequestRepository.findCreationRequestsByIdAndAndStatus(accountCreationRequestId, Status.IN_PROGRESS).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!"));

        User accountCreationRequestUser = accountCreationRequest.getUser();

        AccountCreateDto accountCreateDtoFromCreationRequest = jsonEntitySerializer.serializeJsonToObject(accountCreationRequest.getPayload(), AccountCreateDto.class);

        Account accountToCreate = new Account();
        accountToCreate.setUser(accountCreationRequestUser);
        accountToCreate.setAmount(accountCreateDtoFromCreationRequest.getAmount());
        accountToCreate.setCurrency(Currency.valueOf(accountCreateDtoFromCreationRequest.getCurrency()));
        accountToCreate.setAccountNumber(ibanGenerator.generateIban(accountToCreate.getCurrency().getCountryCode()));
        accountRepository.save(accountToCreate); // TODO: save returned created entity. line 276 redundant

        accountCreationRequest.setStatus(Status.CREATED);

        Long createdAccountId = accountRepository.findAccountByAccountNumber(accountToCreate.getAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account with this account number not found!")).getId();
        accountCreationRequest.setCreatedId(createdAccountId);
        creationRequestRepository.save(accountCreationRequest);

        String userEmail = accountCreationRequestUser.getEmail();
        if (!(userEmail == null)) { // TODO: use != instead of !()
            emailService.sendEmail(userEmail,
                    String.format("Request approved. Id of created account: %d", createdAccountId),
                    approveMessage);
        } else {
            throw new EntityNotFoundException("User email is not found!"); //TODO: to constant
        }
    }

    /**
     * rejectAccountCreationRequest method. Rejects CreationRequest, sets CreationRequest status to REJECTED, then send email message.
     *
     * @param accountCreationRequestId Id of CreationRequest we need to reject.
     */

    public void rejectAccountCreationRequest(Long accountCreationRequestId) {
        CreationRequest accountCreationRequest = creationRequestRepository.findCreationRequestsByIdAndAndStatus(accountCreationRequestId, Status.IN_PROGRESS).orElseThrow(() -> new EntityNotFoundException("Account CreationRequest with this id not found!"));

        User accountCreationRequestUser = accountCreationRequest.getUser();

        accountCreationRequest.setStatus(Status.REJECTED);
        creationRequestRepository.save(accountCreationRequest);

        String userEmail = accountCreationRequestUser.getEmail();

        if (!(userEmail == null)) {  // TODO: use != instead of !()
            emailService.sendEmail(userEmail,
                    "Request rejected.", //TODO: to constants
                    rejectMessage);
        } else {
            throw new EntityNotFoundException("User email is not found!"); //TODO: to constants
        }
    }

    /**
     * checkExpiredAccountCreationRequests method. Marks Request, created more than 4 hours ago, as EXPIRED.
     */

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
