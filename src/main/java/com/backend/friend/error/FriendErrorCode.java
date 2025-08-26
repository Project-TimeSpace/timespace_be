package com.backend.friend.error;

import com.backend.response.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {
	RELATION_NOT_FOUND("FRIEND_001", "친구 관계가 아닙니다."),
	VISIBILITY_ALREADY_SET("FRIEND_012", "이미 해당 공개범위로 설정되어 있습니다."),
	NICKNAME_REQUIRED("FRIEND_003", "별칭은 비어 있을 수 없습니다."),
	REVERSE_RELATION_NOT_FOUND("FRIEND_002", "상대방의 친구 관계가 존재하지 않습니다. 관리자에게 문의하세요." ),

	REQUEST_NOT_FOUND("FRIEND_REQ_001", "존재하지 않는 친구 요청입니다."),
	NO_PERMISSION("FRIEND_REQ_002", "해당 친구 요청에 대한 권한이 없습니다."),
	ALREADY_FRIENDS("FRIEND_REQ_003", "이미 친구 관계입니다."),
	REQUEST_ALREADY_SENT("FRIEND_REQ_004", "이미 요청을 보낸 상태입니다."),

	CALENDAR_PRIVATE("FRIEND_004", "상대방의 캘린더는 비공개입니다.");

	private final String code;
	private final String message;

	@Override public String code()    { return code; }
	@Override public String message() { return message; }
}
