package com.itech.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Edvard Krainiy on 01/04/2022
 */
@Component
public class RabbitMqPublisher {
    @Value("${spring.rabbit.mq.queuename}")
    private String queueName;

    @Autowired
    private AmqpTemplate template;

    public void sendMessageToQueue(String message){
        template.convertAndSend(queueName, message);
    }
}
