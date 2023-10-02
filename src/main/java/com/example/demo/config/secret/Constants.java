package com.example.demo.config.secret;

import com.example.demo.exceptions.CustomException;
import org.springframework.http.HttpStatus;


public abstract class Constants {

    public static final String API_KEY = "SmF2YSBkZXZlbG9wZXIgZnJvbSBJVE1PIQ==";

    public static void validateKey(String apiKey) {
        if(!apiKey.equals(API_KEY)) {
            throw new CustomException("Wrong key", HttpStatus.FORBIDDEN);
        }
    }
}
