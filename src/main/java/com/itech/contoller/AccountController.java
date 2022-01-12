package com.itech.contoller;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.service.account.AccountService;
import com.itech.utils.JsonEntitySerializer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * AccountController. Created to obtain all info about accounts.
 *
 * @author Edvard Krainiy on 12/18/2021
 */

@RestController
@Log4j2
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    private final JsonEntitySerializer jsonEntitySerializer;

    public AccountController(AccountService accountService, JsonEntitySerializer jsonEntitySerializer) {
        this.accountService = accountService;
        this.jsonEntitySerializer = jsonEntitySerializer;
    }

    /**
     * getAllAccounts endpoint.
     *
     * @return List<Account> All accounts in database.
     */

    @ApiOperation(value = "Obtain all accounts.", notes = "Returns all accounts, stored in DB", produces = "application/json", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of accounts."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Accounts not found.")
    })
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAllAccounts());
    }

    /**
     * getAccountById endpoint.
     *
     * @param accountId id of account we want to get.
     * @return ResponseEntity<AccountDto> Response with HTTP code and AccountDto of account that we found.
     */

    @ApiOperation(value = "Obtain account by id.", notes = "Returns account with id we noted.", produces = "application/json", response = AccountDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved account."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account with this id not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id")
                                                     @ApiParam(name = "accountId", value = "Id of account we need to obtain.") Long accountId) {
        return ResponseEntity.ok(accountService.findAccountByAccountId(accountId));
    }


    /**
     * createAccount endpoint.
     *
     * @param accountChangeDto Data-transfer object of account that will be transformed to Account and put into DB.
     * @return ResponseEntity<Long> 201 HTTP code and id of created account.
     */
    @ApiOperation(value = "Create account.", notes = "Creates new account and saves that one into DB.", response = Long.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created account."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account not created.")
    })
    @Transactional
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Long> createAccount(@RequestBody @Valid @ApiParam(name = "accountChangeDto", value = "Dto of account we want to create and save.") AccountCreateDto accountChangeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountChangeDto));
    }

    /**
     * updateAccount endpoint.
     *
     * @param accountUpdateDto Data-transfer object of account that will be transformed to Account and updated.
     * @param accountId        Id of account we need to update.
     * @return ResponseEntity<Void> 204 HTTP code.
     */
    @ApiOperation(value = "Update account.", notes = "Updates account with id we noted.", produces = "application/json", response = AccountDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully updated account."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account not updated.")
    })
    @Transactional
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@RequestBody @Valid @ApiParam(name = "accountUpdateDto", value = "Dto of account, which we use to update other account.") AccountUpdateDto accountUpdateDto,
                                                    @PathVariable("id") @ApiParam(name = "accountId", value = "Id of account we want to update.") Long accountId) {

        if (log.isDebugEnabled()) {
            log.debug("RequestBody: {}", jsonEntitySerializer.serializeObjectToJson(accountUpdateDto));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(accountService.updateAccount(accountUpdateDto, accountId));
    }

    /**
     * deleteAccountById endpoint.
     *
     * @param accountId id of account we want to delete.
     * @return ResponseEntity 204 HTTP code.
     */
    @ApiOperation(value = "Delete account.", notes = "Deletes account with id we noted.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted account."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account not deleted.")
    })
    @Transactional
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable("id") @ApiParam(name = "accountId", value = "Id of account we want to delete.") Long accountId) {
        accountService.deleteAccountByAccountId(accountId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
