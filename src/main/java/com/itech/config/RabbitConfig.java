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
 * @author Edvard Krainiy on 01/03/2022
 */
@Configuration
@EnableRabbit
public class RabbitConfig {
    @Value("${spring.rabbit.mq.queuename}")
    private String queueName;

    @Value("${spring.rabbit.mq.hostname}")
    private String hostname;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(hostname);
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue bankAppQueue() {
        return new Queue(queueName);
    }
}
