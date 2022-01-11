package com.itech.service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * EmailSender class. Configures JavaMailSender for sending messages.
 *
 * @author Edvard Krainiy on 12/9/2021
 */

public class EmailSender {
    @Value("${spring.mail.host}")
    private String hostAddress;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * getJavaMailSender method.
     *
     * @return Returns us configured javaMailSender bean.
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(hostAddress);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); //TODO: move to properties
        props.put("mail.debug", "true"); //TODO: move to properties

        return mailSender;
    }
}
