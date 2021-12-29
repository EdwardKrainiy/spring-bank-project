package com.itech.service.user.impl;

import com.itech.model.enumeration.Role;
import com.itech.model.dto.user.UserDto;
import com.itech.model.entity.User;
import com.itech.repository.UserRepository;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.mail.EmailService;
import com.itech.service.user.UserService;
import com.itech.utils.JwtDecoder;
import com.itech.utils.exception.*;
import com.itech.utils.mapper.user.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UserServiceImpl implements UserService {
    @Value("${spring.mail.confirmation.message}")
    private String confirmMessage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserDtoMapper userDtoMapper;

    /**
     * createUser method. Saves our user on DB.
     *
     * @param userDto User transfer object, which we need to save. This one will be converted into User object, passed some checks and will be saved on DB.
     * @return ResponseEntity Response, which contains message and HTTP code. If something will be wrong, it will throw different Exceptions, which will tell about mistakes and errors.
     * @throws EntityNotFoundException   If user wasn't found.
     * @throws EntityExistsException     if user already exists.
     */
    @Override
    public void createUser(UserDto userDto){

        @Valid User mappedUser = userDtoMapper.toEntity(userDto);

        if (userRepository.getUserByUsername(mappedUser.getUsername()).isPresent() || userRepository.getUserByEmail(mappedUser.getEmail()).isPresent())
            throw new ValidationException("This user already exists!");

        User createdUser = new User(mappedUser.getUsername(), encoder.encode(mappedUser.getPassword()), mappedUser.getEmail(), Role.USER);

        Long createdUserId = userRepository.save(createdUser).getId();

        String confirmationToken = tokenProvider.generateConfirmToken(createdUserId);

        createdUser.setConfirmationToken(confirmationToken);

        userRepository.save(createdUser);

        emailService.sendEmail(userRepository.getUserByRole(Role.MANAGER).orElseThrow(() -> new EntityNotFoundException("User not found!")).getEmail(),
                "Confirm email for user " + mappedUser.getUsername(),
                confirmMessage + confirmationToken);
    }

    /**
     * findUserByUsername method. Finds user by username.
     *
     * @param username Username of the user we need to get from DB.
     * @return User Found by username User object. If user wasn't found, it will throw UserNotFoundException.
     */
    @Override
    public User findUserByUsername(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    }

    /**
     * findUserByUsernameAndPassword method. Finds user by username and password.
     *
     * @param username Username of the user we need to get from DB.
     * @param password Password of the user we need to get from DB.
     * @return User Found by username and password User object. If user wasn't found, it will throw UserNotFoundException.
     */
    @Override
    public User findUserByUsernameAndPassword(String username, String password) {

        User foundUser = userRepository.getUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (foundUser.getPassword().equals(password)) {
            return foundUser;
        } else throw new IncorrectPasswordException(username);
    }

    /**
     * activateUser method. Activates user, found by token.
     *
     * @param token Transferred token of the user we need to activate.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @Transactional
    @Override
    public void activateUser(String token) {
        Long userId = jwtDecoder.getIdFromConfirmToken(token);

        User activatedUser = userRepository.getById(userId);

        if (activatedUser.getConfirmationToken() == null) {
            throw new ValidationException("This user is already activated!");
        }

        activatedUser.setConfirmationToken(null);
        activatedUser.setActivated(true);

        userRepository.save(activatedUser);

        emailService.sendEmail(activatedUser.getEmail(), "Email confirmed", "Your email was confirmed successfully!");
    }
}
