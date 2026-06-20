package com.project.smartmatch.model.request;


import lombok.Getter;
import lombok.Setter;
//@data eklersen getter,setter vb zaten gelir. Hangisini kullanmalıyım?

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
}
