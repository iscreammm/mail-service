package com.iscreamm.mailservice.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("jwt")
public class JwtConfig {
    private String accessSecret;
    private String refreshSecret;
    private int accessExpirationMs;
    private int refreshExpirationMs;

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getRefreshSecret() {
        return refreshSecret;
    }

    public void setRefreshSecret(String refreshSecret) {
        this.refreshSecret = refreshSecret;
    }

    public int getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public void setAccessExpirationMs(int accessExpirationMs) {
        this.accessExpirationMs = accessExpirationMs;
    }

    public int getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public void setRefreshExpirationMs(int refreshExpirationMs) {
        this.refreshExpirationMs = refreshExpirationMs;
    }
}
