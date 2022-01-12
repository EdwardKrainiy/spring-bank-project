package com.itech.rabbit;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RabbitMqListener class. Provides queue listener.
 *
 * @author Edvard Krainiy on 01/03/2022
 */
@Component
@Log4j2
public class RabbitMqListener {
    private final RabbitMqProcessor processor;
    @Value("${spring.rabbit.mq.queuename}")
    private String queueName;

    public RabbitMqListener(RabbitMqProcessor processor) {
        this.processor = processor;
    }

    /**
     * processRabbitQueue method. Creates RabbitMQ listener for queue, which will process messages from queue.
     *
     * @param message Message from queue.
     */

    @RabbitListener(queues = "${spring.rabbit.mq.queuename}")
    public void processRabbitQueue(String message) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Message received from %s: %s", queueName, message));
        } else {
            log.info("Message received!");
        }
        processor.processTransactionMessage(message);
    }
}
