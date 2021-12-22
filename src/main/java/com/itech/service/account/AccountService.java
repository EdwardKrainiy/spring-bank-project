package com.itech.service.account;

import com.itech.model.dto.AccountCreateDto;
import com.itech.model.dto.AccountDto;
import com.itech.model.dto.AccountUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * AccountService interface. Provides us different methods to work with Account objects on Service layer.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
public interface AccountService {
    ResponseEntity<List<AccountDto>> findAllAccounts();

    ResponseEntity<AccountDto> findAccountByAccountId(Long accountId);

    ResponseEntity<Long> createAccount(AccountCreateDto accountCreateDto);

    ResponseEntity<Void> deleteAccountByAccountId(Long accountId);

    ResponseEntity<Void> updateAccount(AccountUpdateDto accountUpdateDto, Long accountId);
}
