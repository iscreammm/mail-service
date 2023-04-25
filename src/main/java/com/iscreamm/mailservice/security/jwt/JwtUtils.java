package com.iscreamm.mailservice.security.jwt;

import com.iscreamm.mailservice.model.User;
import com.iscreamm.mailservice.security.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private final JwtConfig jwtConfig;

    @Autowired
    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateJwtAccessToken(User user) {

        System.out.println(jwtConfig.getAccessSecret() + ":" + jwtConfig.getAccessExpirationMs());

        return generateTokenFromUsername(user.getUsername(),
                jwtConfig.getAccessSecret(), jwtConfig.getAccessExpirationMs());
    }

    public String generateJwtRefreshToken(User user) {
        return generateTokenFromUsername(user.getUsername(),
                jwtConfig.getRefreshSecret(), jwtConfig.getRefreshExpirationMs());
    }

    public String generateTokenFromUsername(String username, String jwtSecret, int jwtExpirationMs) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String getLoginFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getAccessSecret()).parseClaimsJws(token).getBody().getSubject();
    }

    public void validateJwtAccessToken(String authToken) {
        Jwts.parser().setSigningKey(jwtConfig.getAccessSecret()).parseClaimsJws(authToken);
    }

    public void validateJwtRefreshToken(String authToken) {
        Jwts.parser().setSigningKey(jwtConfig.getRefreshSecret()).parseClaimsJws(authToken);
    }
}
