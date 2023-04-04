package com.iscreamm.mailservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String username;

    @Autowired
    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String data) throws MessagingException, JSONException, IOException {
        JSONObject jsonObject = new JSONObject(data);

        String emailTo = jsonObject.getString("emailTo");
        String subject = jsonObject.getString("subject");
        String message = jsonObject.getString("message");

        if (!emailTo.contains("@")) {
            throw new IOException("Email need to contain '@'");

        } else if ((subject.length() == 0) || (message.length() == 0)) {
            throw new IOException("Subject and message can't be empty");

        } else {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            message += "<br>"
                    + "<br>"
                    + "Provided by <a href=\"https://github.com/iscreammm\"> iscreamm </a>";

            helper.setText(message, true);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setFrom(username);

            javaMailSender.send(mimeMessage);
        }
    }
}
