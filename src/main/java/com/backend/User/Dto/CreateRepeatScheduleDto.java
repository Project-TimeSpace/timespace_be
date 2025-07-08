package com.backend.User.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Schema(description = "반복 일정 생성 요청 DTO")
public class CreateRepeatScheduleDto {

    @Schema(description = "일정 제목", example = "반복 회의")
    private String title;

    @Schema(description = "색상 int로 받아와야함", example = "1:Red:Hex Enum")
    private int color;

    @Schema(description = "일정 카테고리 코드", example = "1")
    private Long category_id;

    @Schema(description = "반복 시작일(YYYY-MM-DD)", example = "2025-07-01")
    private LocalDate startDate;

    @Schema(description = "반복 종료일(YYYY-MM-DD)", example = "2025-12-31")
    private LocalDate endDate;

    @Schema(description = "반복 요일 (1=월요일 ~ 7=일요일)", example = "3")
    private int repeatDays;

    @Schema(description = "시작 시간 (HH:mm:ss)", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간 (HH:mm:ss)", example = "10:00:00")
    private LocalTime endTime;

}
