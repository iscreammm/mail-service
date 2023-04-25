package com.iscreamm.mailservice.service;

import com.iscreamm.mailservice.model.User;
import com.iscreamm.mailservice.repository.UserRepository;
import com.iscreamm.mailservice.security.config.JedisConfig;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.io.IOException;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final JedisPool jedisPool;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils, JedisConfig jedisConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.jedisPool = new JedisPool(jedisConfig.getHost(), jedisConfig.getPort());
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

        String accessToken = jwtUtils.generateJwtAccessToken(user);
        String refreshToken = jwtUtils.generateJwtRefreshToken(user);

        updatePair(refreshToken, username);

        return new JwtResponse(accessToken, refreshToken).toString();
    }

    public String refreshToken(String token) throws IOException {
        String username = null;

        jwtUtils.validateJwtRefreshToken(token);

        try(Jedis jedis = jedisPool.getResource()) {
            username = jedis.get(token);
        }

        if (username == null) {
            throw new IOException("Invalid token!");
        }

        User user = userRepository.findByUsername(username);

        String accessToken = jwtUtils.generateJwtAccessToken(user);
        String refreshToken = jwtUtils.generateJwtRefreshToken(user);

        updatePair(refreshToken, username);

        return new JwtResponse(accessToken, refreshToken).toString();
    }

    public void updatePair(String refreshToken, String username) {
        String cursor = "0";
        ScanParams scanParams = new ScanParams().count(1);

        try(Jedis jedis = jedisPool.getResource()) {
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

                if ((scanResult.getResult().size() != 0) && jedis.get(scanResult.getResult().get(0)).equals(username)) {
                    jedis.del(scanResult.getResult().get(0));
                }

                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));

            jedis.set(refreshToken, username);
        }
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
