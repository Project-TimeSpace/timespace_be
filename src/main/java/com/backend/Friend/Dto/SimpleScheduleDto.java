package com.backend.Friend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "간단 일정 조회 DTO (날짜 및 시간만 제공)")
public class SimpleScheduleDto {

    @Schema(description = "일정 날짜", example = "2025-06-15")
    private LocalDate date;

    @Schema(description = "요일 (1=월요일 ~ 7=일요일)", example = "3")
    private int day;

    @Schema(description = "시작 시간", example = "09:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "10:00")
    private LocalTime endTime;
}

