package com.itech.contoller;

import com.itech.model.dto.TransactionDto;
import com.itech.model.entity.Transaction;
import com.itech.service.transaction.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Edvard Krainiy on 12/23/2021
 */
@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    @Autowired
    private TransactionServiceImpl transactionService;

    @GetMapping("{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable("id") Long transactionId){
        return transactionService.findTransactionById(transactionId);
    }

    @GetMapping()
    public ResponseEntity<List<TransactionDto>> getAllTransactions(){
        return transactionService.findAllTransactions();
    }
}
