package com.itech.contoller;

import com.itech.model.dto.user.UserDto;
import com.itech.security.jwt.authentication.JwtAuthenticationByUserDetails;
import com.itech.service.user.UserService;
import com.itech.utils.JsonEntitySerializer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller with sign-in, sign-up and email-confirmation endpoints.
 *
 * @author Edvard Krainiy on 12/3/2021
 */

@RestController
@Log4j2
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails;

    @Autowired
    private JsonEntitySerializer jsonEntitySerializer;

    /**
     * Sign-in endpoint.
     *
     * @param userDto User object we need to sign-in.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody UserDto userDto) throws AuthenticationException{
        log.info("RequestBody: {}", jsonEntitySerializer.serializeObjectToJson(userDto));

        return jwtAuthenticationByUserDetails.authenticate(userDto);
    }

    /**
     * Sign-up endpoint.
     *
     * @param userDto User object we need to sign-up.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody UserDto userDto){
        log.info("RequestBody: {}", jsonEntitySerializer.serializeObjectToJson(userDto));

        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Email-confirmation endpoint.
     *
     * @param token token of user we need to confirm.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @GetMapping("/email-confirmation")
    public ResponseEntity<Void> emailConfirmation(@RequestParam("token") String token) {
        userService.activateUser(token);
        return ResponseEntity.ok().build();
    }
}
