package com.backend.response;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String code();
	String message();
	default HttpStatus status() {
		return HttpStatus.BAD_REQUEST;
	}
}
