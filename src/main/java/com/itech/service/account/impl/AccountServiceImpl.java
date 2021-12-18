package com.itech.service.account.impl;

import com.itech.model.Account;
import com.itech.model.dto.AccountDto;
import com.itech.repository.AccountRepository;
import com.itech.service.account.AccountService;
import com.itech.utils.exception.account.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
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
