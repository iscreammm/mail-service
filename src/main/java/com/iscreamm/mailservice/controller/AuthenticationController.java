package com.iscreamm.mailservice.controller;

import com.iscreamm.mailservice.service.UserService;
import com.iscreamm.mailservice.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> registerUser(@RequestBody String data) throws JSONException, IOException {
        userService.addUser(data);

        return new ResponseEntity<>((new Message<>(true, "", 1)).toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/auth", consumes="application/json")
    public ResponseEntity<String> loginUser(@RequestBody String data) throws JSONException, IOException {
        String userData = userService.auth(data);

        return new ResponseEntity<>((new Message<>(true, "", userData)).toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/refresh/{token}")
    public ResponseEntity<String> refreshToken(@PathVariable String token) throws IOException {
        String data = userService.refreshToken(token);

        return new ResponseEntity<>((new Message<>(true, "", data)).toString(), HttpStatus.OK);
    }
}
