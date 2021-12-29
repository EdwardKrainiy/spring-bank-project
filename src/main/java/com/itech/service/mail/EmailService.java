package com.itech.service.mail;

/**
 * EmailSender interface. Provides us different methods to work with MailSender.
 *
 * @author Edvard Krainiy on 12/10/2021
 */

public interface EmailService {
    void sendEmail(String toAddress, String subject, String message);
}
