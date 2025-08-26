package com.backend.group.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "내가 받은 그룹 초대(가입 요청) DTO")
public class GroupInviteDto {

	@Schema(description = "초대 요청 ID", example = "12")
	private Long requestId;

	@Schema(description = "그룹 ID", example = "7")
	private Long groupId;

	@Schema(description = "그룹 이름", example = "알고리즘 스터디")
	private String groupName;

	@Schema(description = "초대한 사용자 ID", example = "3")
	private Long inviterId;

	@Schema(description = "초대한 사용자 이름", example = "홍길동")
	private String inviterName;

	@Schema(description = "요청 상태(PENDING/ACCEPTED/REJECTED)", example = "PENDING")
	private String status;

	@Schema(description = "요청 일시", example = "2025-06-01T16:00:00")
	private LocalDateTime requestedAt;

}
