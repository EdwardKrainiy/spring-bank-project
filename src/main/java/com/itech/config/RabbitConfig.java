package com.itech.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitConfig class. Provides us queue bean.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@Configuration
public class RabbitConfig {
  @Value("${spring.rabbit.mq.queuename}")
  private String queueName;

  @Bean
  public Queue createBankAppQueue() {
    return new Queue(queueName);
  }
}
