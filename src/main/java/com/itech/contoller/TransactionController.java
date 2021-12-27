package com.itech.contoller;

import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.service.transaction.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Transaction controller with getTransactionById, getAllTransactions and createTransaction endpoints.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    @Autowired
    private TransactionServiceImpl transactionService;

    /**
     * getTransactionById endpoint.
     *
     * @param transactionId Id of transaction we need to obtain.
     * @return ResponseEntity Response, which contains TransactionDto and HTTP code.
     */

    @GetMapping("{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable("id") Long transactionId) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findTransactionById(transactionId));
    }

    /**
     * getAllTransactions endpoint.
     *
     * @return ResponseEntity Response, which contains all transactionDtos and HTTP code.
     */

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findAllTransactions());
    }

    /**
     * createTransaction endpoint.
     *
     * @param transactionCreateDto Request body object, which we want to create in JSON format.
     * @return ResponseEntity Response, which contains id of created transaction and HTTP code.
     */

    @PostMapping
    public ResponseEntity<Long> createTransaction(@Valid @RequestBody TransactionCreateDto transactionCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionCreateDto));
    }

}
