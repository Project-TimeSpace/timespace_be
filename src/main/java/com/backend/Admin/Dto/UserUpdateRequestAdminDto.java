package com.backend.Admin.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(name = "UserUpdateRequestAdminDto", description = "관리자용 사용자 정보 변경 요청 조회 DTO (OLD/NEW 동시 제공)")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestAdminDto {

	// 메타
	private Long requestId;
	private Long userId;

	// OLD(현재 저장된 값)
	private String  oldUserName;
	private Integer oldUnivCode;
	private String  oldPhoneNumber;
	private LocalDate oldBirthDate;

	// NEW(요청된 변경 값; null이면 해당 항목은 변경 없음)
	private String  newUserName;
	private Integer newUnivCode;
	private String  newPhoneNumber;
	private LocalDate newBirthDate;

	// 상태/감사
	private int    statusCode;    // 1/2/3
	private String statusName;    // 대기/수락/거절
	private String reviewReason;
	private LocalDateTime createdAt;
	private LocalDateTime reviewedAt;
}

