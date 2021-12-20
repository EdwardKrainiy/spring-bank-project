package com.itech.service.account;

import com.itech.model.entity.Account;
import com.itech.model.dto.AccountCreateDto;

import java.util.List;

/**
 * AccountService interface. Provides us different methods to work with Account objects on Service layer.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
public interface AccountService {
    List<Account> findAllAccounts();

    Account findAccountByAccountId(Long accountId);

    Long createAccount(AccountCreateDto accountCreateDto);

    void deleteAccountByAccountId(Long accountId);
}
