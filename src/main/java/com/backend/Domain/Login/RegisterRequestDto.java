package com.backend.Domain.Login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String username;
    private String email;
    private String Password;
    private String phoneNumber;
}
