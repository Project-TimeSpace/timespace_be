package com.backend.user.Error;

import com.backend.response.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserScheduleErrorCode implements ErrorCode {
	SCHEDULE_ID_REQUIRED("SCH_000", "일정 ID가 NULL 입니다"),
	SINGLE_SCHEDULE_NOT_FOUND("SCH_001", "단일 일정을 찾을 수 없습니다"),
	FORBIDDEN_SCHEDULE_ACCESS("SCH_002", "해당 일정에 접근 권한이 없습니다"),
	REQUEST_BODY_REQUIRED("SCH_003", "요청 본문이 필요합니다"),
	INVALID_TIME_RANGE("SCH_004", "종료 시간은 시작 시간보다 늦어야 합니다"),
	INVALID_COLOR_CODE("SCH_005", "유효하지 않은 색상 코드입니다"),
	INVALID_DATE_RANGE("SCH_006", "종료 날짜는 시작 날짜 이후여야 합니다"),
	INVALID_DAY_OF_WEEK("SCH_007", "유효하지 않은 요일 값입니다"),
	MISSING_REQUIRED_FIELDS("SCH_008", "필수 항목이 비어 있습니다"),
	REPEAT_SCHEDULE_NOT_FOUND("SCH_009", "반복 일정을 찾을 수 없습니다"),
	OCCURRENCE_DATE_OUT_OF_RANGE("SCH_010", "삭제하려는 날짜가 스케줄 기간에 포함되지 않습니다"),
	REPEAT_OCCURRENCE_ALREADY_EXCEPTION("SCH_011", "이미 예외 처리된 날짜입니다"),
	SCHEDULE_ACCESS_DENIED("SCH_011", "해당 스케쥴의 유저가 아닙니다");

	private final String code;
	private final String message;

	@Override public String code()    { return code; }
	@Override public String message() { return message; }
}
