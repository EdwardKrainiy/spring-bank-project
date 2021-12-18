package com.itech.contoller;

import com.itech.model.Account;
import com.itech.model.dto.AccountDto;
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
    @ResponseBody
    public List<Account> getAllAccounts() {
        return accountService.findAllAccounts();
    }

    /**
     * getAccountById endpoint.
     *
     * @param accountId id of account we want to get.
     * @return Account Entity of account that we found.
     */

    @GetMapping("{id}")
    @ResponseBody
    public Account getAccountById(@PathVariable("id") Long accountId) {
        return accountService.findAccountByAccountId(accountId);
    }


    /**
     * createAccount endpoint.
     *
     * @param accountDto Data-transfer object of account that will be transformed to Account and put into DB.
     * @return ResponseEntity<Long> 201 HTTP code and id of created account.
     */
    @Transactional
    @PostMapping
    public ResponseEntity<Long> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.status(201).body(accountService.createAccount(accountDto));
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
        accountService.deleteAccountByAccountId(accountId);
        return ResponseEntity.status(204).build();
    }
}
