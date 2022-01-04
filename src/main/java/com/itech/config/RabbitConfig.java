package com.itech.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for RabbitMQ.
 *
 * @author Edvard Krainiy on 01/03/2022
 */
@Configuration
@EnableRabbit
public class RabbitConfig {
    @Value("${spring.rabbit.mq.queuename}")
    private String queueName;

    @Value("${spring.rabbit.mq.hostname}")
    private String hostname;

    /**
     * connectionFactory method.
     *
     * @return ConnectionFactory bean.
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(hostname);
    }

    /**
     * amqpAdmin method. Creates amqp admin.
     *
     * @return AmqpAdmin bean.
     */

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    /**
     * rabbitTemplate method.
     *
     * @return RabbitTemplate bean.
     */

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    /**
     * bankAppQueue method. Creates queue.
     *
     * @return Queue bean.
     */

    @Bean
    public Queue bankAppQueue() {
        return new Queue(queueName);
    }
}
