package com.iscreamm.mailservice.service;

import com.iscreamm.mailservice.security.config.MailSenderConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;
    private final MailSenderConfig mailSenderConfig;

    @Autowired
    public MailSenderService(JavaMailSender javaMailSender, MailSenderConfig mailSenderConfig) {
        this.javaMailSender = javaMailSender;
        this.mailSenderConfig = mailSenderConfig;
    }

    public void send(String data) throws MessagingException, JSONException, IOException {
        JSONObject jsonObject = new JSONObject(data);

        JSONArray emails = jsonObject.getJSONArray("emailTo");
        String subject = jsonObject.getString("subject");
        String message = jsonObject.getString("message");

        for (int i = 0; i < emails.length(); i++) {
            String emailTo = emails.getString(i);

            if (!emailTo.contains("@")) {
                throw new IOException("Email need to contain '@'");

            } else if ((subject.length() == 0) || (message.length() == 0)) {
                throw new IOException("Subject and message can't be empty");

            } else {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                String additionalText = "<br>"
                        + "<br>"
                        + "Provided by <a href=\"https://github.com/iscreammm\"> iscreamm </a>";

                helper.setText(message + additionalText, true);
                helper.setTo(emailTo);
                helper.setSubject(subject);
                helper.setFrom(mailSenderConfig.getUsername());

                javaMailSender.send(mimeMessage);
            }
        }
    }
}
