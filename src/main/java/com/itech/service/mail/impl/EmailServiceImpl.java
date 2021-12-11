package com.itech.service.mail.impl;

import com.itech.service.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService interface. Provides us sending messages method.
 * @autor Edvard Krainiy on ${date}
 * @version 1.0
 */

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * sendSimpleEmail method.
     * @param toAddress Adress we want to send the message.
     * @param subject Message subject.
     * @param message Text of the message.
     */
    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }
}
