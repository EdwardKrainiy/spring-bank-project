package com.itech.contoller;

import com.itech.model.User;
import com.itech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@ComponentScan(value = "com.itech")

public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/sign-in")
    public String signIn(@RequestParam("email") String email,
                       @RequestParam("password") String password){
        return "Sign-in!";
    }

    @GetMapping("/sign-up")
    public String signUp(@RequestParam("email") String email,
                       @RequestParam("password") String password){
        userService.createUser(new User(email, password));
        return "Succeed signUp!";
    }

    @GetMapping("/email-confirmation")
        public void emailConfirmation(@RequestParam("userId") Long id,
                                      @RequestParam("token") String token){
    }
}
