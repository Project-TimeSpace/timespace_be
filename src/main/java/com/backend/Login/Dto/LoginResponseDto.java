package com.backend.Login.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String type;
}
