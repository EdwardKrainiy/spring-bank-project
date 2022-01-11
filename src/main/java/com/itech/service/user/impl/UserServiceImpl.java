package com.itech.service.user.impl;

import com.itech.model.dto.user.UserSignUpDto;
import com.itech.model.entity.User;
import com.itech.model.enumeration.Role;
import com.itech.repository.UserRepository;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.mail.EmailService;
import com.itech.service.user.UserService;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.EntityNotFoundException;
import com.itech.utils.exception.IncorrectPasswordException;
import com.itech.utils.exception.ValidationException;
import com.itech.utils.mapper.user.UserSignUpDtoMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

/**
 * Implementation of UserService interface. Provides us different methods of Service layer to work with Repository layer of User objects.
 *
 * @author Edvard Krainiy on 12/10/2021
 */

@Service
@Log4j2
@PropertySources({
        @PropertySource("classpath:properties/exception.properties"),
        @PropertySource("classpath:properties/mail.properties")
})
public class UserServiceImpl implements UserService {
    @Value("${mail.confirmation.message}")
    private String confirmMessage;

    @Value("${exception.user.already.exists}")
    private String userIsAlreadyExistsExceptionText;

    @Value("${exception.user.not.found}")
    private String userNotFoundExceptionText;

    @Value("${exception.user.is.already.activated}")
    private String userIsAlreadyActivatedExceptionText;

    @Value("${mail.user.confirmation.title}")
    private String userConfirmationMessageTitleText;

    @Value("${mail.user.successful.confirmation.title}")
    private String successfulConfirmationTitle;

    @Value("${mail.user.successful.confirmation.message}")
    private String successfulConfirmationMessage;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final EmailService emailService;

    private final TokenProvider tokenProvider;

    private final JwtDecoder jwtDecoder;

    private final UserSignUpDtoMapper userSignUpDtoMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder, EmailService emailService, TokenProvider tokenProvider, JwtDecoder jwtDecoder, UserSignUpDtoMapper userSignUpDtoMapper) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.emailService = emailService;
        this.tokenProvider = tokenProvider;
        this.jwtDecoder = jwtDecoder;
        this.userSignUpDtoMapper = userSignUpDtoMapper;
    }

    @Override
    public void createUser(UserSignUpDto userDto) {

        @Valid User mappedUser = userSignUpDtoMapper.toEntity(userDto);

        if (userRepository.findUserByUsername(mappedUser.getUsername()).isPresent() || userRepository.findUserByEmail(mappedUser.getEmail()).isPresent())
            throw new ValidationException(userIsAlreadyExistsExceptionText);

        User createdUser = new User(mappedUser.getUsername(), encoder.encode(mappedUser.getPassword()), mappedUser.getEmail(), Role.USER);

        Long createdUserId = userRepository.save(createdUser).getId();

        String confirmationToken = tokenProvider.generateConfirmToken(createdUserId);

        createdUser.setConfirmationToken(confirmationToken);

        userRepository.save(createdUser);

        emailService.sendEmail(userRepository.findUserByRole(Role.MANAGER).orElseThrow(() -> new EntityNotFoundException(userNotFoundExceptionText)).getEmail(),
                String.format(userConfirmationMessageTitleText, mappedUser.getUsername()),
                String.format(("%s%s"), confirmMessage, confirmationToken));

        log.info("Mail with confirmation link was sent to manager's email.");
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException(userNotFoundExceptionText));
    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {

        User foundUser = userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException(userNotFoundExceptionText));

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
            throw new ValidationException(userIsAlreadyActivatedExceptionText);
        }

        activatedUser.setConfirmationToken(null);
        activatedUser.setActivated(true);

        userRepository.save(activatedUser);

        emailService.sendEmail(activatedUser.getEmail(), successfulConfirmationTitle, successfulConfirmationMessage);
        log.info("Mail about confirmation was sent.");
    }
}
