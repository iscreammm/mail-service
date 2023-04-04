package com.iscreamm.mailservice.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JwtResponse {
    private final String token;
    private final String type = "Bearer";

    public JwtResponse(String token) {
        this.token = token;
    }

    @Override
    public String toString() {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(this);
    }
}
