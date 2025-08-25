package com.backend.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INVALID_DATE_RANGE("COMMON_001", "endDate는 startDate보다 빠를 수 없습니다.");

	private final String code;
	private final String message;

	@Override public String code()    { return code; }
	@Override public String message() { return message; }
}
