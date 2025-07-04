package com.backend.User.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryDto {
    @Schema(description = "카테고리 ID", example = "3")
    private Integer categoryId;

    @Schema(description = "카테고리 이름", example = "학교 수업")
    private String categoryName;

    @Schema(description = "카테고리 색상(ScheduleColor Enum)", example = "BLUE")
    private String color;  // ScheduleColor.name() 사용
}
