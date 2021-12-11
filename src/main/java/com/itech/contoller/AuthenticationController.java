package com.itech.contoller;

import com.itech.model.dto.UserDto;
import com.itech.security.jwt.authentication.JwtAuthenticationByUserDetails;
import com.itech.service.user.UserService;
import com.itech.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller with sign-in, sign-up and email-confirmation endpoints.
 * @autor Edvard Krainiy on ${date}
 * @version 1.0
 */

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails;

    /**
     * Sign-in endpoint.
     * @param userDto User object we need to sign-in.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @PostMapping("/sign-in")
    public ResponseEntity signIn (@RequestBody UserDto userDto) throws AuthenticationException {
        return jwtAuthenticationByUserDetails.authenticate(userDto);
    }

    /**
     * Sign-up endpoint.
     * @param userDto User object we need to sign-up.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @PostMapping("/sign-up")
    public ResponseEntity signUp(@RequestBody UserDto userDto){
        ResponseEntity entity;
        try {
            entity = userService.createUser(userDto);
        } catch (EmptyUsernameException | UserNotFoundException | EmptyPasswordException | EmptyEmailException | InvalidEmailException | UserExistsException | IncorrectPasswordLengthException e) {
            e.printStackTrace();
            entity = ResponseEntity.badRequest().body(e.getMessage());
        }
        return entity;
    }

    /**
     * Email-confirmation endpoint.
     * @param token token of user we need to confirm.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @GetMapping("/email-confirmation")
        public ResponseEntity emailConfirmation(@RequestParam("token") String token){
        return userService.activateUser(token);
    }
}
