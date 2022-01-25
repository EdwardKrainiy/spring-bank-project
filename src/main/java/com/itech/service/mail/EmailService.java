package com.itech.service.mail;

/**
 * EmailSender interface. Provides us different methods to work with MailSender.
 *
 * @author Edvard Krainiy on 12/10/2021
 */
public interface EmailService {

  /**
   * sendEmail method.
   *
   * @param toAddress Address we want to send the message.
   * @param subject Message subject.
   * @param message Text of the message.
   */
  void sendEmail(String toAddress, String subject, String message);
}
