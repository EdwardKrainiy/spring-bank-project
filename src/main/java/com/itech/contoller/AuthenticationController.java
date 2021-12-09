package com.itech.contoller;

import com.itech.model.dto.UserDto;
import com.itech.security.jwt.authentication.JwtAuthenticationByUserDetails;
import com.itech.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")

public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn (@RequestBody UserDto userDto) throws AuthenticationException {
        return jwtAuthenticationByUserDetails.authenticate(userDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto){
        return userService.createUser(userDto);
    }

    @GetMapping("/email-confirmation")
        public ResponseEntity<?> emailConfirmation(@RequestParam("token") String token){
        return userService.activateUser(token);
    }
}
