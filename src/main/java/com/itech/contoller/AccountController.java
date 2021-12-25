package com.itech.contoller;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.service.account.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AccountController. Created to obtain all info about accounts.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    /**
     * getAllAccounts endpoint.
     *
     * @return List<Account> All accounts in database.
     */

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return accountService.findAllAccounts();
    }

    /**
     * getAccountById endpoint.
     *
     * @param accountId id of account we want to get.
     * @return ResponseEntity<AccountDto> Response with HTTP code and AccountDto of account that we found.
     */

    @GetMapping("{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id") Long accountId) {
        return accountService.findAccountByAccountId(accountId);
    }


    /**
     * createAccount endpoint.
     *
     * @param accountCreateDto Data-transfer object of account that will be transformed to Account and put into DB.
     * @return ResponseEntity<Long> 201 HTTP code and id of created account.
     */
    @Transactional
    @PostMapping
    public ResponseEntity<Long> createAccount(@RequestBody AccountCreateDto accountCreateDto) {
        return accountService.createAccount(accountCreateDto);
    }

    /**
     * updateAccount endpoint.
     *
     * @param accountUpdateDto Data-transfer object of account that will be transformed to Account and updated.
     * @param accountId        Id of account we need to update.
     * @return ResponseEntity<Void> 204 HTTP code.
     */

    @Transactional
    @PutMapping("{id}")
    public ResponseEntity<Void> updateAccount(@RequestBody AccountUpdateDto accountUpdateDto,
                                              @PathVariable("id") Long accountId) {
        return accountService.updateAccount(accountUpdateDto, accountId);
    }

    /**
     * deleteAccountById endpoint.
     *
     * @param accountId id of account we want to delete.
     * @return ResponseEntity 204 HTTP code.
     */
    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable("id") Long accountId) {
        return accountService.deleteAccountByAccountId(accountId);
    }
}
