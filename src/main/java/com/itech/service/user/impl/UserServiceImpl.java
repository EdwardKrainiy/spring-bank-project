package com.itech.service.user.impl;

import com.itech.model.Role;
import com.itech.model.User;
import com.itech.model.dto.UserDto;
import com.itech.repository.UserRepository;
import com.itech.utils.JwtDecoder;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.mail.EmailService;
import com.itech.service.user.UserService;
import com.itech.utils.DtoMappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService{
    private static final String VALID_EMAIL_ADDRESS_REGEX =  "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoMappingUtils dtoMappingUtils;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public ResponseEntity<?> createUser(UserDto userDto) {

        User mappedUser = dtoMappingUtils.DtoToUser(userDto);

        if(mappedUser.getUsername() == null){
            return ResponseEntity.badRequest()
                    .body("Missing username!");
        }

        if(mappedUser.getPassword() == null){
            return ResponseEntity.badRequest()
                    .body("Missing password!");
        }

        if(mappedUser.getEmail() == null){
            return ResponseEntity.badRequest()
                    .body("Missing email!");
        }

        if(!mappedUser.getEmail().matches(VALID_EMAIL_ADDRESS_REGEX)){
            return ResponseEntity.badRequest()
                    .body("Incorrect email pattern!");
        }

        if(userRepository.getUserByUsername(mappedUser.getUsername()) != null){
            return ResponseEntity.badRequest()
                    .body("User with this username already exists!");
        }

        if(userRepository.getUserByEmail(mappedUser.getEmail()) != null){
            return ResponseEntity.badRequest()
                    .body("User with this email already exists!");
        }

        if(mappedUser.getPassword().length() > 10 || mappedUser.getPassword().length() == 0){
            return ResponseEntity.badRequest()
                            .body("Incorrect password length! Length must be from 0 to 10 symbols.");
        }

        userRepository.save(new User(mappedUser.getUsername(), encoder.encode(mappedUser.getPassword()), mappedUser.getEmail(), Role.USER));

        Long createdUserId = userRepository.getUserByUsername(userDto.getUsername()).getId();

        String confirmationToken = tokenProvider.generateConfirmToken(createdUserId);

        emailService.sendSimpleEmail(userRepository.getUserByRole(Role.MANAGER).getEmail(),
                "Confirm email for user " + mappedUser.getUsername(),
                "This user is signing up. Confirm his email and activate the account following this link: " + "http://localhost:8080/api/auth/email-confirmation?token=" + confirmationToken);

        return ResponseEntity.ok("Successful sign-up!");
    }

    @Override
    public User findUserByUsername(String username) {
       return userRepository.getUserByUsername(username);

    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        User foundUser = userRepository.getUserByUsername(username);
        if(foundUser != null){
            if(foundUser.getPassword().equals(password)){
                return foundUser;
            }
        }
        return null;
    }

    @Transactional
    @Override
    public ResponseEntity<?> activateUser(String token) {
        Long userId = jwtDecoder.getIdFromConfirmToken(token);

        System.out.println(userId);

        if(userId == null){
            return ResponseEntity.badRequest().body("Incorrect user id!");
        }
        else{
            userRepository.activateUser(userId);
            return ResponseEntity.ok("User sucessfully activated!");
        }

    }
}
