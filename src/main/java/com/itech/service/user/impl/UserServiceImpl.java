package com.itech.service.user.impl;

import com.itech.model.Role;
import com.itech.model.User;
import com.itech.model.dto.UserDto;
import com.itech.repository.UserRepository;
import com.itech.utils.JwtDecoder;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.mail.EmailService;
import com.itech.service.user.UserService;
import com.itech.utils.DtoMapper;
import com.itech.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService interface. Provides us different methods of Service layer to work with Repository layer of User objects.
 * @author Edvard Krainiy on 12/10/2021
 */

@Service
public class UserServiceImpl implements UserService{
    private static final String VALID_EMAIL_ADDRESS_REGEX =  "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

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
    private DtoMapper dtoMapper;

    /**
     * createUser method. Saves our user on DB.
     * @param userDto User transfer object, which we need to save. This one will be converted into User object, passed some checks and will be saved on DB.
     * @return ResponseEntity Response, which contains message and HTTP code. If something will be wrong, it will throw different Exceptions, which will tell about mistakes and errors.
     * @throws UserValidationException All user validation errors, like missing email or password, invalid email or others.
     * @throws UserNotFoundException If user wasn't found.
     * @throws UserExistsException if user already exists.
     */
    @Override
    public ResponseEntity<String> createUser(UserDto userDto) throws UserValidationException, UserNotFoundException, UserExistsException {

        User mappedUser = dtoMapper.toEntity(userDto);

        if(mappedUser.getUsername() == null) throw new UserValidationException("Missing username!");


        if(mappedUser.getPassword() == null) throw new UserValidationException("Missing password!");


        if(mappedUser.getEmail() == null) throw new UserValidationException("Missing email!");


        if(!mappedUser.getEmail().matches(VALID_EMAIL_ADDRESS_REGEX)) throw new UserValidationException("Email is not valid!");


        if(userRepository.getUserByUsername(mappedUser.getUsername()).isPresent() || userRepository.getUserByEmail(mappedUser.getEmail()).isPresent()) throw new UserExistsException();


        if(mappedUser.getPassword().length() > 20 || mappedUser.getPassword().length() < 5) throw new UserValidationException("Incorrect password length! It must be from 5 to 20");


        Long createdUserId = userRepository.save(new User(mappedUser.getUsername(), encoder.encode(mappedUser.getPassword()), mappedUser.getEmail(), Role.USER)).getId();

        String confirmationToken = tokenProvider.generateConfirmToken(createdUserId);

        emailService.sendEmail(userRepository.getUserByRole(Role.MANAGER).orElseThrow(() -> new UserNotFoundException(Role.MANAGER)).getEmail(),
                "Confirm email for user " + mappedUser.getUsername(),
                "This user is signing up. Confirm his email and activate the account following this link: " + "http://localhost:8080/api/auth/email-confirmation?token=" + confirmationToken);

        return ResponseEntity.ok("Successful sign-up!");
    }

    /**
     * findUserByUsername method. Finds user by username.
     * @param username Username of the user we need to get from DB.
     * @return User Found by username User object. If user wasn't found, it will throw UserNotFoundException.
     */
    @Override
    public User findUserByUsername(String username){

       User user = userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
       return user;
    }

    /**
     * findUserByUsernameAndPassword method. Finds user by username and password.
     * @param username Username of the user we need to get from DB.
     * @param password Password of the user we need to get from DB.
     * @return User Found by username and password User object. If user wasn't found, it will throw UserNotFoundException.
     */
    @Override
    public User findUserByUsernameAndPassword(String username, String password){

        User foundUser = userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if(foundUser.getPassword().equals(password)){
            return foundUser;
        }

        else throw new IncorrectPasswordException(username);
    }

    /**
     * activateUser method. Activates user, found by token.
     * @param token Transferred token of the user we need to activate.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @Transactional
    @Override
    public ResponseEntity<String> activateUser(String token){
        Long userId = jwtDecoder.getIdFromConfirmToken(token);

        emailService.sendEmail(userRepository.getById(userId).getEmail(), "Email confirmed", "Your email was confirmed successfully!");
        return ResponseEntity.ok("User successfully activated!");
    }
}
