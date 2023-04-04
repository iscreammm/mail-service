package com.iscreamm.mailservice.controller;

import com.iscreamm.mailservice.service.UserService;
import com.iscreamm.mailservice.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/auth", consumes="application/json")
    public String registerUser(@RequestBody String data) throws JSONException, IOException {
        userService.addUser(data);

        return (new Message<>(true, "", 1)).toString();
    }

    @GetMapping(path = "/auth", consumes="application/json")
    public String loginUser(@RequestBody String data) throws JSONException, IOException {
        String userData = userService.auth(data);

        return (new Message<>(true, "", userData)).toString();
    }
}
