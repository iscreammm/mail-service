package com.iscreamm.mailservice.service;

import com.iscreamm.mailservice.model.User;
import com.iscreamm.mailservice.repository.UserRepository;
import com.iscreamm.mailservice.security.jwt.JwtUtils;
import com.iscreamm.mailservice.utils.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public void addUser(String data) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject(data);

        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        if (username.length() > 16) {
            throw new IOException("Username length must be less than 16!");

        } else if (!isUsernameFree(username)) {
            throw new IOException("Username is busy!");

        } else if (password.length() > 16) {
            throw new IOException("Password length must be less than 16!");
        }

        password = passwordEncoder.encode(password);
        User user = new User(username, password);

        userRepository.save(user);
    }

    public String auth(String data) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject(data);

        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new IOException("Username isn't correct");

        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IOException("Password isn't correct");
        }

        String token = getToken(user);

        return new JwtResponse(token).toString();
    }

    public String getToken(User user) {
        String token;

        if (user.getToken() == null) {
            token = jwtUtils.generateJwtAccessToken(user);

            user.setToken(token);
            userRepository.save(user);
        } else {
            token = user.getToken();
        }

        return token;
    }

    public User getUser(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username isn't correct");

        } else {
            return user;
        }
    }

    public boolean isUsernameFree(String username) {
        return (userRepository.findByUsername(username) == null);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = getUser(login);

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getAuthorities());
    }
}
