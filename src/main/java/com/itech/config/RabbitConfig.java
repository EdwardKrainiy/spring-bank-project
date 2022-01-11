package com.itech.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitConfig class. Provides us queue bean.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Configuration
public class RabbitConfig {
    @Bean
    public Queue createBankAppQueue(){
        return new Queue("bankAppQueue"); // TODO: move queue name to configuration file.
    }
}
