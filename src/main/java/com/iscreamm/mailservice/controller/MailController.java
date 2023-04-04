package com.iscreamm.mailservice.controller;

import com.iscreamm.mailservice.service.MailSenderService;
import com.iscreamm.mailservice.utils.Message;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MailController {

    private final MailSenderService mailSenderService;

    @Autowired
    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping(path = "/mail", consumes="application/json")
    public String sendMail(@RequestBody String data) throws MessagingException, JSONException, IOException {
        mailSenderService.send(data);

        return (new Message<>(true, "", 1)).toString();
    }
}
