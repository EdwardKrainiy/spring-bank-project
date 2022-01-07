package com.itech.contoller;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.service.request.RequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("api")
public class CreationRequestController {

    @Autowired
    private RequestService requestService;

    /**
     * getCreationRequestById endpoint. Returns CreationRequest by Id.
     *
     * @param transactionCreationRequestId Id of transactionCreationRequestId we want to obtain.
     * @return ResponseEntity<CreationRequestDto> Response, which contains HTTP code and Dto of obtained creationRequest.
     */

    @GetMapping("/transactions/creation-requests/{id}")
    public ResponseEntity<CreationRequestDto> getTransactionCreationRequestById(@PathVariable("id") Long transactionCreationRequestId) {
        return ResponseEntity.ok(requestService.findTransactionCreationRequestById(transactionCreationRequestId));
    }

    /**
     * getCreationRequestById endpoint. Returns CreationRequest by Id.
     *
     * @param accountCreationRequestId Id of transactionCreationRequestId we want to obtain.
     * @return ResponseEntity<CreationRequestDto> Response, which contains HTTP code and Dto of obtained creationRequest.
     */

    @GetMapping("/accounts/creation-requests/{id}")
    public ResponseEntity<CreationRequestDto> getAccountCreationRequestById(@PathVariable("id") Long accountCreationRequestId) {
        return ResponseEntity.ok(requestService.findAccountCreationRequestById(accountCreationRequestId));
    }

    /**
     * getAccountCreationRequests endpoint. Returns all accountCreationRequests.
     *
     * @return ResponseEntity<List<CreationRequestDto>> Response, which contains HTTP code and all Dtos of obtained accountCreationRequests.
     */

    @GetMapping("/accounts/creation-requests")
    public ResponseEntity<List<CreationRequestDto>> getAccountCreationRequests() {
        return ResponseEntity.ok(requestService.findAccountCreationRequests());
    }

    /**
     * getTransactionCreationRequests endpoint. Returns all transactionCreationRequests.
     *
     * @return ResponseEntity<List<CreationRequestDto>> Response, which contains HTTP code and all Dtos of obtained transactionCreationRequests.
     */

    @GetMapping("/transactions/creation-requests")
    public ResponseEntity<List<CreationRequestDto>> getTransactionCreationRequests() {
        return ResponseEntity.ok(requestService.findTransactionCreationRequests());
    }

    /**
     * approveAccountCreationRequest endpoint. Approves CreationRequest and creates account based on payload of CreationRequest, sets CreationRequest status to CREATED, then send email message.
     *
     * @param accountCreationRequest Id of accountCreationRequestId we need to approve.
     * @return ResponseEntity<Void> Response, which contains HTTP code.
     */

    @GetMapping("/accounts/creation-requests/{id}/approve")
    public ResponseEntity<Void> approveAccountCreationRequest(@PathVariable("id") Long accountCreationRequest) {
        requestService.approveAccountCreationRequest(accountCreationRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * rejectAccountCreationRequest endpoint. Rejects CreationRequest, sets CreationRequest status to REJECTED, then send email message.
     *
     * @param accountCreationRequest Id of accountCreationRequestId we need to reject.
     * @return ResponseEntity<Void> Response, which contains HTTP code.
     */

    @GetMapping("/accounts/creation-requests/{id}/reject")
    public ResponseEntity<Void> rejectAccountCreationRequest(@PathVariable("id") Long accountCreationRequest) {
        requestService.rejectAccountCreationRequest(accountCreationRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
