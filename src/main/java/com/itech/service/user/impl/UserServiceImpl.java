package com.itech.service.user.impl;

import com.itech.model.dto.user.UserSignInDto;
import com.itech.model.dto.user.UserSignUpDto;
import com.itech.model.entity.User;
import com.itech.model.enumeration.Role;
import com.itech.repository.UserRepository;
import com.itech.security.jwt.authentication.JwtAuthenticationByUserDetails;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.mail.EmailService;
import com.itech.service.user.UserService;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.IncorrectPasswordException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.literal.ExceptionMessageText;
import com.itech.utils.literal.LogMessageText;
import com.itech.utils.literal.PropertySourceClasspath;
import com.itech.utils.mapper.user.UserSignUpDtoMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService interface. Provides us different methods of Service layer to work
 * with Repository layer of User objects.
 *
 * @author Edvard Krainiy on 12/10/2021
 */
@Service
@Log4j2
@PropertySource(PropertySourceClasspath.MAIL_PROPERTIES_CLASSPATH)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final EmailService emailService;
  private final TokenProvider tokenProvider;
  private final JwtDecoder jwtDecoder;
  private final UserSignUpDtoMapper userSignUpDtoMapper;
  private final JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails;

  @Value("${mail.confirmation.message}")
  private String confirmMessage;

  @Value("${mail.user.confirmation.title}")
  private String userConfirmationMessageTitleText;

  @Value("${mail.user.successful.confirmation.title}")
  private String successfulConfirmationTitle;

  @Value("${mail.user.successful.confirmation.message}")
  private String successfulConfirmationMessage;

  @Override
  public void createUser(UserSignUpDto userDto) {

    User mappedUser = userSignUpDtoMapper.toEntity(userDto);

    if (userRepository.findUserByUsername(mappedUser.getUsername()).isPresent()
        || userRepository.findUserByEmail(mappedUser.getEmail()).isPresent()) {
      log.error(LogMessageText.USER_IS_ALREADY_EXISTS_LOG);
      throw new ValidationException(ExceptionMessageText.USER_IS_ALREADY_EXISTS);
    }

    User createdUser =
        new User(
            mappedUser.getUsername(),
            encoder.encode(mappedUser.getPassword()),
            mappedUser.getEmail(),
            Role.USER);

    Long createdUserId = userRepository.save(createdUser).getId();

    String confirmationToken = tokenProvider.generateConfirmToken(createdUserId);

    createdUser.setConfirmationToken(confirmationToken);

    userRepository.save(createdUser);

    Optional<User> managerUserOptional = userRepository.findUserByRole(Role.MANAGER);
    if (!managerUserOptional.isPresent()) {
      log.error(LogMessageText.MANAGER_USER_NOT_EXISTS_LOG);
      throw new EntityNotFoundException(ExceptionMessageText.USER_NOT_FOUND);
    } else {
      emailService.sendEmail(
          managerUserOptional.get().getEmail(),
          String.format(userConfirmationMessageTitleText, mappedUser.getUsername()),
          String.format(("%s%s"), confirmMessage, confirmationToken));

      log.info(
          String.format(LogMessageText.MESSAGE_SENT_LOG, managerUserOptional.get().getEmail()));
    }
  }

  @Override
  public User findUserByUsername(String username) {
    return userRepository
        .findUserByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.USER_NOT_FOUND));
  }

  @Override
  public User findUserByUsernameAndPassword(String username, String password) {

    User foundUser =
        userRepository
            .findUserByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.USER_NOT_FOUND));

    if (foundUser.getPassword().equals(password)) {
      return foundUser;
    } else throw new IncorrectPasswordException(username);
  }

  @Transactional
  @Override
  public void activateUser(String token) {
    Long userId = jwtDecoder.getIdFromConfirmToken(token);

    User activatedUser = userRepository.getById(userId);

    if (activatedUser.getConfirmationToken() == null) {
      throw new ValidationException(ExceptionMessageText.USER_IS_ALREADY_ACTIVATED);
    }

    activatedUser.setConfirmationToken(null);
    activatedUser.setActivated(true);

    userRepository.save(activatedUser);

    emailService.sendEmail(
        activatedUser.getEmail(), successfulConfirmationTitle, successfulConfirmationMessage);
    log.info(String.format(LogMessageText.MESSAGE_SENT_LOG, activatedUser.getEmail()));
  }

  public boolean isUserActivated(User user) {
    return user.getConfirmationToken() == null && user.isActivated();
  }

  @Override
  public ResponseEntity<String> authenticateUser(UserSignInDto userSignInDto) {
    User userToSignIn =
        userRepository
            .findUserByUsername(userSignInDto.getUsername())
            .orElseThrow(() -> new EntityNotFoundException(ExceptionMessageText.USER_NOT_FOUND));
    if (isUserActivated(userToSignIn)) {
      return jwtAuthenticationByUserDetails.authenticate(userSignInDto);
    } else throw new ValidationException(ExceptionMessageText.USER_NOT_ACTIVATED);
  }
}
