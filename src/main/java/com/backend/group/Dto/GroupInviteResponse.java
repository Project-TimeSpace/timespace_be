package com.backend.group.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.backend.group.Entity.GroupRequest;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "내가 받은 그룹 초대(가입 요청) 응답 DTO")
public class GroupInviteResponse {

	@Schema(description = "그룹 ID", example = "7")
	private Long groupId;

	@Schema(description = "그룹 이름", example = "알고리즘 스터디")
	private String groupName;

	@Schema(description = "초대한 사용자 이름", example = "홍길동")
	private String inviterName;

	@Schema(description = "초대한 사용자 이메일", example = "hong@test.com")
	private String inviterEmail;

	@Schema(description = "요청 상태(PENDING/ACCEPTED/REJECTED)", example = "PENDING/ACCEPTED/REJECTED")
	private String status;

	@Schema(description = "요청 일시", example = "2025-06-01T16:00:00")
	private LocalDateTime requestedAt;

	public static GroupInviteResponse from(GroupRequest gr) {
		return GroupInviteResponse.builder()
			.groupId(gr.getGroup().getId())
			.groupName(gr.getGroup().getGroupName())
			.inviterName(gr.getInviter().getUserName())
			.inviterEmail(gr.getInviter().getEmail())
			.requestedAt(gr.getRequestedAt())
			.build();
	}
}
