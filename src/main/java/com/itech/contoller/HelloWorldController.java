package com.itech.contoller;

import io.swagger.annotations.Api;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloWorldController {

    @GetMapping("/helloworld")
    public String helloWorld(Model model){
        return "Hello, world!";
    }
}
