package com.itech.contoller;

import com.itech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/sign-in")
    public String signIn(@RequestParam("username") String username,
                       @RequestParam("password") String password){
        
        return "Sign-in!";
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam("email") String email){
        return userService.createUser(username, password, email);
    }

    @GetMapping("/email-confirmation")
        public void emailConfirmation(@RequestParam("userId") Long id,
                                      @RequestParam("token") String token){
    }
}
