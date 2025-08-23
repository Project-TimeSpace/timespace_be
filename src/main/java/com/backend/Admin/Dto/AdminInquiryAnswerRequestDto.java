package com.backend.Admin.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "AdminInquiryAnswerRequest", description = "관리자 문의 답변 요청 DTO")
public class AdminInquiryAnswerRequestDto {

	@Schema(description = "관리자 답변 내용", example = "안녕하세요, 문의 주신 사항에 대해 안내드립니다...")
	@NotBlank
	private String replyContent;
}
