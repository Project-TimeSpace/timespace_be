package com.backend.User.Error;

import com.backend.response.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_ID_REQUIRED("USER_000", "ID가 NULL 입니다"),
	USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다");

	private final String code;
	private final String message;

	@Override public String code()    { return code; }
	@Override public String message() { return message; }
}

