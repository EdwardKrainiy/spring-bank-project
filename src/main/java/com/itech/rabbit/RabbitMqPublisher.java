package com.itech.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RabbitMqPublisher class. Provides sendMessage method.
 *
 * @author Edvard Krainiy on 01/04/2022
 */
@Component
@RequiredArgsConstructor
public class RabbitMqPublisher {
    private final AmqpTemplate template;
    @Value("${spring.rabbit.mq.queuename}")
    private String queueName;

    /**
     * sendMessageToQueue method. Sends message to queue.
     *
     * @param message Message, that we will send to queue.
     */

    public void sendMessageToQueue(String message) {
        template.convertAndSend(queueName, message);
    }
}
