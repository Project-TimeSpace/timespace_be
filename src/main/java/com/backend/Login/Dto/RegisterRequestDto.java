package com.backend.Login.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String email;
    private String Password;
    private String username;
    private Integer univCode;
}
