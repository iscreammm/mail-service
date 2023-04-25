package com.iscreamm.mailservice.exception;

import com.iscreamm.mailservice.utils.Message;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.security.SignatureException;

@RestControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(JSONException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(JSONException e) {
        Message<Integer> message = new Message<>(false, "Can't parse input json: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(SignatureException e) {
        Message<Integer> message = new Message<>(false, "Invalid JWT signature: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(MalformedJwtException e) {
        Message<Integer> message = new Message<>(false, "Invalid JWT token: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(ExpiredJwtException e) {
        Message<Integer> message = new Message<>(false, "JWT token is expired: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(UnsupportedJwtException e) {
        Message<Integer> message = new Message<>(false, "JWT token is unsupported: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        Message<Integer> message = new Message<>(false, "JWT claims string is empty: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(MessagingException e) {
        Message<Integer> message = new Message<>(false, "Mail sending error: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(IOException e) {
        Message<Integer> message = new Message<>(false, "Input error: " + e.getMessage(), -1);

        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }
}
