package com.backend.user.Error;

import com.backend.response.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_ID_REQUIRED("USER_000", "ID가 NULL 입니다"),
	USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다"),
	USER_EMAIL_NOT_FOUND("USER_002", "해당 이메일의 사용자를 찾을 수 없습니다"),
	UPDATE_FIELDS_REQUIRED("USER_010", "변경할 항목이 없습니다"),
	UPDATE_REQUEST_ALREADY_PENDING("USER_011", "대기 중인 개인정보 변경 요청이 이미 존재합니다"),
	INQUIRY_OPEN_EXISTS("USER_020", "진행 중인 문의가 있어 새 문의를 등록할 수 없습니다"),
	INQUIRY_FIELDS_REQUIRED("USER_021", "문의 제목/내용이 비어 있습니다"),
	;

	private final String code;
	private final String message;

	@Override public String code()    { return code; }
	@Override public String message() { return message; }
}

