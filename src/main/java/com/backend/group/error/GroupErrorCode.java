package com.backend.group.error;

import com.backend.response.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GroupErrorCode implements ErrorCode {
	GROUP_ID_REQUIRED("GROUP_000", "그룹 ID가 NULL 입니다."),
	GROUP_NOT_FOUND("GROUP_001", "그룹을 찾을 수 없습니다."),
	GROUP_NAME_REQUIRED("GROUP_002", "그룹 이름은 비어 있을 수 없습니다."),
	NOT_GROUP_MEMBER("GROUP_003", "그룹 멤버가 아닙니다."),
	NOT_GROUP_OWNER("GROUP_004", "그룹 마스터만 수행할 수 있습니다."),
	MEMBER_NOT_FOUND("GROUP_005", "그룹 멤버를 찾을 수 없습니다."),
	MEMBER_ALREADY_EXISTS("GROUP_006", "이미 그룹 멤버입니다."),
	INVITE_ALREADY_SENT("GROUP_007", "이미 초대가 발송되었습니다."),
	INVITE_NOT_FOUND("GROUP_008", "초대를 찾을 수 없습니다."),
	MASTER_CANNOT_LEAVE("GROUP_009", "마스터는 탈퇴할 수 없습니다. 위임 또는 그룹 삭제가 필요합니다."),
	INVALID_STATUS("GROUP_010", "유효하지 않은 상태 값입니다."),
	MEMBERSHIP_NOT_FOUND("GROUP_002", "그룹 멤버십이 존재하지 않습니다."),
	ALREADY_GROUP_MEMBER("GROUP_003", "이미 그룹의 멤버입니다."),
	INVITE_TARGET_NOT_FOUND("GROUP_005", "초대 대상 사용자를 찾을 수 없습니다."),
	NO_PERMISSION("GROUP_006", "그룹 접근 권한이 없습니다."),
	GROUP_INVITE_NOT_FOUND("GROUP_REQ_001", "초대가 없습니다."),
	GROUP_SCHEDULE_NOT_FOUND("GROUP_SCH_001", "그룹 일정이 존재하지 않습니다.");


	private final String code;
	private final String message;

	@Override public String code()    { return code; }
	@Override public String message() { return message; }
}
