package com.backend.User.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Schema(description = "단일 스케쥴 생성 요청 DTO")
@Builder
public class CreateSingleScheduleDto {

    @Schema(description = "일정 제목", example = "회의")
    private String title;

    @Schema(description = "일정 표시 색상-정수로 저장하고 Enum으로 매핑", example = "1")
    private Integer color;

    @Schema(description = "일정 날짜(YYYY-MM-DD)", example = "2025-07-01")
    private LocalDate date;

    @Schema(description = "시작 시간 (HH:mm:ss)", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간 (HH:mm:ss)", example = "10:00:00")
    private LocalTime endTime;
}

