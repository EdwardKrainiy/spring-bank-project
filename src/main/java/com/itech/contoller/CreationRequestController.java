package com.itech.contoller;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.service.account.AccountService;
import com.itech.service.transaction.TransactionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Authentication controller with different endpoints to manipulate with CreationRequest objects.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@RestController
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreationRequestController {

    private final TransactionService transactionService;

    private final AccountService accountService;

    /**
     * getCreationRequestById endpoint. Returns CreationRequest by Id.
     *
     * @param transactionCreationRequestId Id of transactionCreationRequestId we want to obtain.
     * @return ResponseEntity<CreationRequestDto> Response, which contains HTTP code and Dto of obtained creationRequest.
     */
    @ApiOperation(value = "Obtain transaction creation request by id.", notes = "Returns transaction creation request with id we noted.", produces = "application/json", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved transaction creation request."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Transaction creation request with this id not found.")
    })
    @GetMapping("/transactions/creation-requests/{id}")
    public ResponseEntity<CreationRequestDto> getTransactionCreationRequestById(@PathVariable("id") @ApiParam(name = "id", value = "Id of transaction creation request we want to obtain.", required = true) Long transactionCreationRequestId) {
        return ResponseEntity.ok(transactionService.findTransactionCreationRequestById(transactionCreationRequestId));
    }

    /**
     * getCreationRequestById endpoint. Returns CreationRequest by Id.
     *
     * @param accountCreationRequestId Id of transactionCreationRequestId we want to obtain.
     * @return ResponseEntity<CreationRequestDto> Response, which contains HTTP code and Dto of obtained creationRequest.
     */
    @ApiOperation(value = "Obtain account creation request by id.", notes = "Returns account creation request with id we noted.", produces = "application/json", response = CreationRequestDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved account creation request."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account creation request with this id not found.")
    })
    @GetMapping("/accounts/creation-requests/{id}")
    public ResponseEntity<CreationRequestDto> getAccountCreationRequestById(@PathVariable("id") @ApiParam(name = "id", value = "Id of account creation request we want to obtain.", required = true) Long accountCreationRequestId) {
        return ResponseEntity.ok(accountService.findAccountCreationRequestById(accountCreationRequestId));
    }

    /**
     * getAccountCreationRequests endpoint. Returns all accountCreationRequests.
     *
     * @return ResponseEntity<List < CreationRequestDto>> Response, which contains HTTP code and all Dtos of obtained accountCreationRequests.
     */
    @ApiOperation(value = "Obtain all account creation requests.", notes = "Returns all account creation requests.", produces = "application/json", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of account creation requests."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account creation requests not found.")
    })
    @GetMapping("/accounts/creation-requests")
    public ResponseEntity<List<CreationRequestDto>> getAccountCreationRequests() {
        return ResponseEntity.ok(accountService.findAccountCreationRequests());
    }

    /**
     * getTransactionCreationRequests endpoint. Returns all transactionCreationRequests.
     *
     * @return ResponseEntity<List < CreationRequestDto>> Response, which contains HTTP code and all Dtos of obtained transactionCreationRequests.
     */
    @ApiOperation(value = "Obtain all transaction creation requests.", notes = "Returns all transaction creation requests.", produces = "application/json", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of transaction creation requests."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Transaction creation requests not found.")
    })
    @GetMapping("/transactions/creation-requests")
    public ResponseEntity<List<CreationRequestDto>> getTransactionCreationRequests() {
        return ResponseEntity.ok(transactionService.findTransactionCreationRequests());
    }

    /**
     * approveAccountCreationRequest endpoint. Approves CreationRequest and creates account based on payload of CreationRequest, sets CreationRequest status to CREATED, then send email message.
     *
     * @param accountCreationRequestId Id of accountCreationRequestId we need to approve.
     * @return ResponseEntity<Void> Response, which contains HTTP code.
     */
    @ApiOperation(value = "Approve account creation request.", notes = "Approves account creation request, creates account and sends message about approvement to email.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully approved account creation request and account created."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account creation request with this id not found.")
    })
    @GetMapping("/accounts/creation-requests/{id}/approve")
    public ResponseEntity<Void> approveAccountCreationRequest(@PathVariable("id") @ApiParam(name = "id", value = "Id of account creation request we want to approve.", required = true) Long accountCreationRequestId) {
        accountService.approveAccountCreationRequest(accountCreationRequestId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * rejectAccountCreationRequest endpoint. Rejects CreationRequest, sets CreationRequest status to REJECTED, then send email message.
     *
     * @param accountCreationRequestId Id of accountCreationRequestId we need to reject.
     * @return ResponseEntity<Void> Response, which contains HTTP code.
     */
    @ApiOperation(value = "Reject account creation request.", notes = "Rejects account creation request and sends message about rejection to email.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully rejected account creation request."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Account creation request with this id not found.")
    })
    @GetMapping("/accounts/creation-requests/{id}/reject")
    public ResponseEntity<Void> rejectAccountCreationRequest(@PathVariable("id") @ApiParam(name = "id", value = "Id of account creation request we want to reject.", required = true) Long accountCreationRequestId) {
        accountService.rejectAccountCreationRequest(accountCreationRequestId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
