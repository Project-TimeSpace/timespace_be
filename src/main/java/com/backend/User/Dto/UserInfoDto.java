package com.backend.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private String userName;
    private String email;
    private String university;
    private String major;
    private String phoneNumber;
    private String selfMemo;
}