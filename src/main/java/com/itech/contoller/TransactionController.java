package com.itech.contoller;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.model.dto.transaction.TransactionCreateDto;
import com.itech.model.dto.transaction.TransactionDto;
import com.itech.service.request.RequestService;
import com.itech.service.transaction.TransactionService;
import com.itech.utils.JsonEntitySerializer;
import com.itech.utils.literal.ExceptionMessageText;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final RequestService requestService;

    private final JsonEntitySerializer jsonEntitySerializer;

    /**
     * getTransactionById endpoint.
     *
     * @param transactionId Id of transaction we need to obtain.
     * @return ResponseEntity Response, which contains TransactionDto and HTTP code.
     */
    @ApiOperation(value = "Obtain transaction by id.", notes = "Returns transaction with id we noted.", produces = "application/json", response = TransactionDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved transaction."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "Transaction with this id not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable("id") @ApiParam(name = "id", value = "Id of transaction we need to obtain.") Long transactionId) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findTransactionById(transactionId));
    }

    /**
     * getAllTransactions endpoint.
     *
     * @return ResponseEntity Response, which contains all transactionDtos and HTTP code.
     */
    @ApiOperation(value = "Obtain all transactions.", notes = "Returns all transactions, stored in DB", produces = "application/json", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of transactions."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findAllTransactions());
    }

    /**
     * createTransaction endpoint.
     *
     * @param transactionCreateDto Request body object, which we want to create in JSON format.
     * @return ResponseEntity Response, which contains id of CreationRequestDto and HTTP code.
     */
    @ApiOperation(value = "Create transaction.", notes = "Creates new transaction and saves that one into DB.", response = CreationRequestDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created transaction."),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 400, message = "Transaction not created.")
    })
    @PostMapping
    public ResponseEntity<CreationRequestDto> createTransaction(@RequestBody @Valid @ApiParam(name = "transactionCreateDto", value = "Dto of transaction we want to create and save.") TransactionCreateDto transactionCreateDto) {
        if (log.isDebugEnabled()) {
            log.debug(String.format(ExceptionMessageText.DEBUG_REQUEST_BODY_LOG_TEXT, jsonEntitySerializer.serializeObjectToJson(transactionCreateDto)));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.processCreationRequestMessage(transactionCreateDto));
    }
}
