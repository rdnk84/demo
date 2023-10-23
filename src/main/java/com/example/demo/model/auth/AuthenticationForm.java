package com.example.demo.model.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AuthenticationForm {

    /**
     * Required user credential field
     */
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String username;
    /**
     * Required user credential field
     */
    @NotEmpty(message = "Password should not be empty")
    private String password;
}
