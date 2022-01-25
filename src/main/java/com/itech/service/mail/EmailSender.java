package com.itech.service.mail;

import com.itech.utils.literal.PropertySourceClasspath;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

/**
 * EmailSender class. Configures JavaMailSender for sending messages.
 *
 * @author Edvard Krainiy on 12/9/2021
 */
@PropertySource(PropertySourceClasspath.MAIL_PROPERTIES_CLASSPATH)
@Component
public class EmailSender {
  @Value("${spring.mail.host}")
  private String hostAddress;

  @Value("${spring.mail.port}")
  private int port;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.transport.protocol}")
  private String transportProtocol;

  @Value("${spring.mail.smtp.auth}")
  private String smtpAuth;

  @Value("${spring.mail.smtp.starttls.enable}")
  private String enableStartTls;

  @Value("${spring.mail.debug}")
  private String mailDebug;

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
    props.put("mail.transport.protocol", transportProtocol);
    props.put("mail.smtp.auth", smtpAuth);
    props.put("mail.smtp.starttls.enable", enableStartTls);
    props.put("mail.debug", mailDebug);

    return mailSender;
  }
}
