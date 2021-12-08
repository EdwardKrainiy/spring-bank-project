package com.itech.service.mail;

public interface EmailService {
    void sendSimpleEmail(String toAddress, String subject, String message);
}
