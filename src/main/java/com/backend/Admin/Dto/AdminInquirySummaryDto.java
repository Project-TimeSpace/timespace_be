package com.backend.Admin.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "관리자용 문의 요약 DTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminInquirySummaryDto {
	@Schema(description = "문의 ID", example = "1")
	private Long inquiryId;

	@Schema(description = "문의 제목", example = "앱 로그인 오류 문의")
	private String title;

	@Schema(description = "문의자 사용자 ID", example = "42")
	private Long userId;

	@Schema(description = "문의자 이름", example = "홍성문")
	private String userName;

	@Schema(description = "문의 상태 (0: 진행 중, 1: 처리 완료)", example = "0")
	private Integer status;

	@Schema(description = "등록 일시", example = "2025-08-06T10:15:30")
	private String createdAt;
}
