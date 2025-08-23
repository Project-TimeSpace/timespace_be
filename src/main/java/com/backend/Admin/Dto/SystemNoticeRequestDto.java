package com.backend.Admin.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SystemNoticeRequestDto {
	@Schema(description = "공지 제목", example = "긴급 점검 안내")
	@NotBlank private String title;

	@Schema(description = "공지 내용", example = "8/20(수) 23:00~24:00 서버 점검 예정")
	@NotBlank private String content;
}