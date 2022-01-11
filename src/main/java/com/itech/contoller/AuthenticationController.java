package com.itech.contoller;

import com.itech.model.dto.user.UserSignInDto;
import com.itech.model.dto.user.UserSignUpDto;
import com.itech.security.jwt.authentication.JwtAuthenticationByUserDetails;
import com.itech.service.user.UserService;
import com.itech.utils.JsonEntitySerializer;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Authentication controller with sign-in, sign-up and email-confirmation endpoints.
 *
 * @author Edvard Krainiy on 12/3/2021
 */

@RestController
@Log4j2
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;

    private final JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails;

    private final JsonEntitySerializer jsonEntitySerializer;

    public AuthenticationController(UserService userService, JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails, JsonEntitySerializer jsonEntitySerializer) {
        this.userService = userService;
        this.jwtAuthenticationByUserDetails = jwtAuthenticationByUserDetails;
        this.jsonEntitySerializer = jsonEntitySerializer;
    }

    /**
     * Sign-in endpoint.
     *
     * @param userSignInDto User object we need to sign-in.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody @Valid UserSignInDto userSignInDto) throws AuthenticationException {

        if (log.isDebugEnabled()) {
            log.debug("RequestBody: {}", jsonEntitySerializer.serializeObjectToJson(userSignInDto));
        }

        return jwtAuthenticationByUserDetails.authenticate(userSignInDto);
    }

    /**
     * Sign-up endpoint.
     *
     * @param userSignUpDto User object we need to sign-up.
     * @return ResponseEntity Response, which contains message and HTTP code.
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Valid UserSignUpDto userSignUpDto) {

        if (log.isDebugEnabled()) {
            log.debug("RequestBody: {}", jsonEntitySerializer.serializeObjectToJson(userSignUpDto));
        }

        userService.createUser(userSignUpDto);
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
