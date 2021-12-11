package com.itech.service.mail;

/**
 * EmailSender interface. Provides us different methods to work with MailSender.
 * @author Edvard Krainiy on ${date}
 * @version 1.0
 */

public interface EmailService {
    void sendSimpleEmail(String toAddress, String subject, String message);
}
