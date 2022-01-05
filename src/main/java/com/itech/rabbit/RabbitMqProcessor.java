package com.itech.rabbit;

import com.itech.service.transaction.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMqProcessor class. Processes message in queue.
 *
 * @author Edvard Krainiy on 01/04/2022
 */
@Component
public class RabbitMqProcessor {
    @Autowired
    private TransactionServiceImpl transactionService;

    /**
     * processTransactionMessage method. Processes message in queue. In our case this message is creationRequestDto object in JSON format.
     * @param creationRequestDtoJson JSON of creationRequestDto object.
     */

    public void processTransactionMessage(String creationRequestDtoJson) {
        transactionService.createTransaction(creationRequestDtoJson);
    }
}
