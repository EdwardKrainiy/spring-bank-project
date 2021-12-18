package com.itech.service.account.impl;

import com.itech.model.entity.Account;
import com.itech.model.Currency;
import com.itech.model.EUCountry;
import com.itech.model.dto.AccountDto;
import com.itech.repository.AccountRepository;
import com.itech.service.account.AccountService;
import com.itech.utils.mapper.AccountDtoMapper;
import com.itech.utils.exception.account.AccountNotFoundException;
import com.itech.utils.exception.account.AccountValidationException;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.IbanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Implementation of AccountService interface. Provides us different methods of Service layer to work with Repository layer of Account objects.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountDtoMapper accountDtoMapper;

    /**
     * findAllAccounts method. Finds all accounts from DB.
     *
     * @return List<Account> List of all found accounts.
     */

    @Override
    public List<Account> findAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) throw new AccountNotFoundException();

        return accounts;
    }

    /**
     * findAccountByAccountId. Finds account by id.
     *
     * @param accountId Id of account we need to find.
     * @return Account Found account entity.
     */

    @Override
    public Account findAccountByAccountId(Long accountId) {
        return accountRepository.findAccountById(accountId).orElseThrow(AccountNotFoundException::new);
    }

    /**
     * createAccount method. Creates account from JSON object in RequestBody and saves into DB.
     *
     * @param accountDto Account transfer object, which we need to save. This one will be converted into Account object, passed some checks and will be saved on DB.
     * @return Long Id of created account.
     */

    @Override
    public Long createAccount(AccountDto accountDto) {
        Account accountEntity = accountDtoMapper.toEntity(accountDto);

        Currency accountCurrency = accountEntity.getCurrency();

        if(accountCurrency == null) throw new AccountValidationException("Missing currency!");

        Iban iban = null;

        ArrayList<EUCountry> euCountriesCodes = new ArrayList<>(EnumSet.allOf(EUCountry.class));

        switch (accountCurrency){
            case BYN: iban = Iban.random(CountryCode.BY);
                break;
            case USD: iban = Iban.random(CountryCode.US);
                break;
            case EUR:
                int randomCountry = (int)Math.floor(Math.random() * euCountriesCodes.size());
                iban = Iban.random(CountryCode.valueOf(euCountriesCodes.get(randomCountry).toString()));
                break;
        }

        IbanUtil.validate(iban.toString());

        accountEntity.setAccountNumber(iban.toString());

        return accountRepository.save(accountEntity).getId();
    }

    /**
     * deleteAccountByAccountId method. Deletes account by id.
     *
     * @param accountId If of account we need to delete.
     */

    @Override
    public void deleteAccountByAccountId(Long accountId) {
        Account foundAccountToDelete = accountRepository.findAccountById(accountId).orElseThrow(AccountNotFoundException::new);
        accountRepository.deleteAccountById(foundAccountToDelete.getId());
    }
}
