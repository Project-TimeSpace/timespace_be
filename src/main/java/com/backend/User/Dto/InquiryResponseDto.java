package com.backend.User.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponseDto {

	@Schema(description = "문의 ID", example = "1")
	private Long inquiryId;

	@Schema(description = "문의 제목", example = "앱 로그인 오류 문의")
	private String title;

	@Schema(description = "문의 내용", example = "앱 로그인 시 토큰이 만료되었다는 오류가 발생합니다.")
	private String content;

	@Schema(description = "문의 상태 (0: 진행 중, 1: 처리 완료)", example = "0")
	private Integer status;

	@Schema(description = "관리자 답변 내용", example = "서버 시간을 확인해 주세요.")
	private String replyContent;

	@Schema(description = "답변 일시", example = "2025-08-06T12:05:00")
	private String answeredAt;

	@Schema(description = "생성 일시", example = "2025-08-06T10:15:30")
	private String createdAt;

	@Schema(description = "수정 일시(admin 동시성 제어)", example = "2025-08-06T12:00:00")
	private String updatedAt;
}
