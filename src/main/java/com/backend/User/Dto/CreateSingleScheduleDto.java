package com.backend.User.Dto;

import com.backend.Config.GlobalEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Schema(description = "단일 스케쥴 생성 요청 DTO")
public class CreateSingleScheduleDto {

    @Schema(description = "일정 제목", example = "회의")
    private String title;

    @Schema(description = "일정 표시 색상-정수로 저장하고 Enum으로 매핑", example = "1:RED:HEX")
    private Integer color;

    @Schema(description = "일정 카테고리 코드 (1=NORMAL, 2=FRIEND, 3=TEAMPLAY, 4=CLUB, 5=SCHOOL)", example = "1")
    private int category;

    @Schema(description = "일정 날짜(YYYY-MM-DD)", example = "2025-07-01")
    private LocalDate date;

    @Schema(description = "요일 (1=월요일 ~ 7=일요일)", example = "3")
    private int day;

    @Schema(description = "시작 시간 (HH:mm:ss)", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간 (HH:mm:ss)", example = "10:00:00")
    private LocalTime endTime;
}

