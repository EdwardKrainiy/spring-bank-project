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
    @Value("${spring.rabbit.mq.queuename}")
    private String queueName;

    /**
     * processRabbitQueue method. Creates RabbitMQ listener for queue, which will process messages from queue.
     *
     * @param message Message from queue.
     */

    @RabbitListener(queues = "${spring.rabbit.mq.queuename}")
    public void processRabbitQueue(String message) {
        log.info(String.format("Message received from %s: " + message, queueName));
    }
}
