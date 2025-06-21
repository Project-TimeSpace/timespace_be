package com.backend.User.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {
    private String userName;
    private String email;
    private String university;
    private String major;
    private String phoneNumber;
    private String selfMemo;
}