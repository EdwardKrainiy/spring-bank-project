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
 * @autor Edvard Krainiy on ${date}
 * @version 1.0
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

    /**
     * createUser method. Saves our user on DB.
     * @param userDto User transfer object, which we need to save. This one will be converted into User object, passed some checks and will be saved on DB.
     * @return ResponseEntity Response, which contains message and HTTP code. If something will be wrong, it will throw different Exceptions, which will tell about mistakes and errors.
     * @throws EmptyUsernameException If username is empty.
     * @throws UserNotFoundException If user wasn't found.
     * @throws EmptyPasswordException If password is empty.
     * @throws EmptyEmailException If email is empty.
     * @throws InvalidEmailException If email is invalid(Validity checks by regex pattern).
     * @throws UserExistsException if user already exists.
     * @throws IncorrectPasswordLengthException If password length is incorrect(> 10 or == 0)
     */
    @Override
    public ResponseEntity createUser(UserDto userDto) throws EmptyUsernameException, UserNotFoundException, EmptyPasswordException, EmptyEmailException, InvalidEmailException, UserExistsException, IncorrectPasswordLengthException {

        User mappedUser = DtoMapper.INSTANCE.DtoUserToUser(userDto);

        if(mappedUser.getUsername() == null) throw new EmptyUsernameException();


        if(mappedUser.getPassword() == null) throw new EmptyPasswordException();


        if(mappedUser.getEmail() == null) throw new EmptyEmailException();


        if(!mappedUser.getEmail().matches(VALID_EMAIL_ADDRESS_REGEX)) throw new InvalidEmailException();


        if(userRepository.getUserByUsername(mappedUser.getUsername()).isPresent() || userRepository.getUserByEmail(mappedUser.getEmail()).isPresent()) throw new UserExistsException();


        if(mappedUser.getPassword().length() > 10 || mappedUser.getPassword().length() == 0) throw new IncorrectPasswordLengthException();


        Long createdUserId = userRepository.save(new User(mappedUser.getUsername(), encoder.encode(mappedUser.getPassword()), mappedUser.getEmail(), Role.USER)).getId();

        String confirmationToken = tokenProvider.generateConfirmToken(createdUserId);

        try {
            emailService.sendSimpleEmail(userRepository.getUserByRole(Role.MANAGER).orElseThrow(() -> new UserNotFoundException(Role.MANAGER)).getEmail(),
                    "Confirm email for user " + mappedUser.getUsername(),
                    "This user is signing up. Confirm his email and activate the account following this link: " + "http://localhost:8080/api/auth/email-confirmation?token=" + confirmationToken);

        } catch (UserNotFoundException exception){
            exception.printStackTrace();
        }

        return ResponseEntity.ok("Successful sign-up!");
    }

    /**
     * findUserByUsername method. Finds user by username.
     * @param username Username of the user we need to get from DB.
     * @return User Found by username User object. If user wasn't found, it will throw UserNotFoundException.
     */
    @Override
    public User findUserByUsername(String username){
        User user = null;
        try{
            user = userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        }catch (UserNotFoundException exception){
            exception.printStackTrace();
        }
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
        User foundUser = null;
        try{
            foundUser = userRepository.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

            if(foundUser.getPassword().equals(password)){
                return foundUser;
            }

            else throw new IncorrectPasswordException(username);

        } catch (UserNotFoundException | IncorrectPasswordException exception){
            exception.printStackTrace();
        }

        return foundUser;
    }

    /**
     * activateUser method. Activates user, found by token.
     * @param token Transferred token of the user we need to activate.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @Transactional
    @Override
    public ResponseEntity activateUser(String token) {
        Long userId = jwtDecoder.getIdFromConfirmToken(token);

        if(userId == null){
            return ResponseEntity.badRequest().body("Incorrect user id!");
        }
        else{
            userRepository.activateUser(userId);
            emailService.sendSimpleEmail(userRepository.getById(userId).getEmail(), "Email confirmed", "Your email was confirmed successfully!");
            return ResponseEntity.ok("User successfully activated!");
        }
    }
}
