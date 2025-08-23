package com.backend.Admin.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemNoticeResponseDto {
	@Schema(description = "공지 ID", example = "1")
	private Integer id;

	@Schema(description = "공지 제목", example = "긴급 점검 안내")
	@NotBlank
	private String title;

	@Schema(description = "공지 내용", example = "8/20(수) 23:00~24:00 서버 점검이 예정되어 있습니다.")
	@NotBlank private String content;
}
