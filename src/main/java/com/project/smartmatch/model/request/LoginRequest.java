package com.project.smartmatch.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

//@data eklersen getter,setter vb zaten gelir. Hangisini kullanmalıyım?
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email field cannot be blank")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password field cannot be blank")
    private String password;
}










