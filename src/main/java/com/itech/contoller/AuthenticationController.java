package com.itech.contoller;

import com.itech.security.jwt.authentication.JwtAuthenticationByUserDetails;
import com.itech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/auth")

public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationByUserDetails jwtAuthenticationByUserDetails;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn (@RequestBody Map<String, String> userDetails) throws AuthenticationException {
        return jwtAuthenticationByUserDetails.authenticate(userDetails);
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestBody String username,
                         @RequestBody String password,
                         @RequestBody String email){
        return userService.createUser(username, password, email);
    }

    @GetMapping("/email-confirmation")
        public void emailConfirmation(@RequestParam("userId") Long id,
                                      @RequestParam("token") String token){
    }
}
