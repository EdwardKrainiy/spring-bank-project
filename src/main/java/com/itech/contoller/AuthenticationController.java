package com.itech.contoller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itech.model.User;
import com.itech.security.jwt.provider.TokenProvider;
import com.itech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")

public class AuthenticationController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn (@RequestBody Map<String, String> userDetails) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDetails.get("username"),
                        userDetails.get("password")
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(token);
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
