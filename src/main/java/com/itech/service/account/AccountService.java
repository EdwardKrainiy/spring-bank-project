package com.itech.service.account;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * AccountService interface. Provides us different methods to work with Account objects on Service layer.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
public interface AccountService {
    List<AccountDto> findAllAccounts();

    AccountDto findAccountByAccountId(Long accountId);

    Long createAccount(AccountCreateDto accountCreateDto);

    void deleteAccountByAccountId(Long accountId);

    void updateAccount(AccountUpdateDto accountUpdateDto, Long accountId);
}
