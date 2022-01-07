package com.itech.service.account.impl;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.model.entity.Account;
import com.itech.model.entity.CreationRequest;
import com.itech.model.entity.User;
import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Currency;
import com.itech.model.enumeration.Status;
import com.itech.repository.AccountRepository;
import com.itech.repository.CreationRequestRepository;
import com.itech.repository.UserRepository;
import com.itech.service.account.AccountService;
import com.itech.utils.IbanGenerator;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.mapper.account.AccountCreateDtoMapper;
import com.itech.utils.mapper.account.AccountDtoMapper;
import com.itech.utils.mapper.account.AccountUpdateDtoMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of AccountService interface. Provides us different methods of Service layer to work with Repository layer of Account objects.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountDtoMapper accountDtoMapper;

    @Autowired
    private AccountCreateDtoMapper accountCreateDtoMapper;

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

    /**
     * findAllAccounts method. Finds all accounts from DB.
     *
     * @return ResponseEntity<List < AccountDto>> ResponseEntity with HTTP code and list of all found accounts.
     */

    @Override
    public List<AccountDto> findAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) throw new EntityNotFoundException("Account not found!");

        List<AccountDto> accountDtos = new ArrayList<>();
        accounts.forEach(account -> accountDtos.add(accountDtoMapper.toDto(account)));

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
        return accountDtoMapper.toDto(accountRepository.findAccountById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found!")));
    }

    /**
     * createAccount method. Creates account from JSON object in RequestBody and saves into DB.
     *
     * @param accountCreateDto Account transfer object, which we need to save. This one will be converted into Account object, passed some checks and will be saved on DB.
     * @return ResponseEntity<Long> ResponseEntity with HTTP code and id of created account.
     */

    @Override
    public Long createAccount(AccountCreateDto accountCreateDto) {
        User foundUser = userRepository.getUserByUsername(jwtDecoder.getUsernameOfLoggedUser()).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        CreationRequest accountCreatingRequest = new CreationRequest();
        accountCreatingRequest.setStatus(Status.IN_PROGRESS);
        accountCreatingRequest.setCreationType(CreationType.ACCOUNT);
        accountCreatingRequest.setPayload(serializer.serializeObjectToJson(accountCreateDto));
        accountCreatingRequest.setUser(foundUser);

        log.info("Account was created successfully!");
        return creationRequestRepository.save(accountCreatingRequest).getId();
    }

    /**
     * updateAccount method. Updates account by id and accountUpdateDto entity.
     *
     * @param accountUpdateDto Account transfer object, which we need to update. This one will be converted into Account object, passed some checks and will be updated on DB.
     * @param accountId        Id of account we need to update.
     * @return ResponseEntity<Long> ResponseEntity with HTTP code and id of updated account.
     */

    @Override
    public void updateAccount(AccountUpdateDto accountUpdateDto, Long accountId) {
        Account updateAccount = accountUpdateDtoMapper.toEntity(accountUpdateDto);
        updateAccount.setId(accountId);
        updateAccount.setAccountNumber(ibanGenerator.generateIban(accountUpdateDto.getCurrency().getCountryCode()));
        accountRepository.save(updateAccount);
    }

    /**
     * deleteAccountByAccountId method. Deletes account by id.
     *
     * @param accountId Id of account we need to delete.
     * @return ResponseEntity<Void> 204 HTTP code.
     */

    @Override
    public void deleteAccountByAccountId(Long accountId) {
        Account foundAccountToDelete = accountRepository.findAccountById(accountId).orElseThrow(() -> new EntityNotFoundException("Account not found!"));
        accountRepository.deleteAccountById(foundAccountToDelete.getId());
    }
}
