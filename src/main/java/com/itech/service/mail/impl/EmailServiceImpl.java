package com.itech.service.mail.impl;

import com.itech.service.mail.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService interface. Provides us sending messages method.
 *
 * @author Edvard Krainiy on 12/9/2021
 */

@Service
@Log4j2
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * sendEmail method.
     *
     * @param toAddress Address we want to send the message.
     * @param subject   Message subject.
     * @param message   Text of the message.
     */
    @Override
    public void sendEmail(String toAddress, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        log.info("AddressTo set.");
        simpleMailMessage.setTo(toAddress);
        log.info("Subject set.");
        simpleMailMessage.setSubject(subject);
        log.info("Text set.");
        simpleMailMessage.setText(message);
        log.info("Mail sending...");
        emailSender.send(simpleMailMessage);
        log.info("Mail was sent successfully!");
    }
}
