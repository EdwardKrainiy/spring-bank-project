package com.itech.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RabbitMqPublisher class. Provides sendMessage method.
 *
 * @author Edvard Krainiy on 01/04/2022
 */
@Component
public class RabbitMqPublisher {
    @Value("${emp.rabbitmq.requestqueue}")
    private String queueName;

    @Autowired
    private AmqpTemplate template; //TODO: constructor injection

    /**
     * sendMessageToQueue method. Sends message to queue.
     *
     * @param message Message, that we will send to queue.
     */

    public void sendMessageToQueue(String message){
        template.convertAndSend(queueName, message);
    }
}
