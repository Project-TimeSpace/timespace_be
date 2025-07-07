package com.backend.User.Dto;

import com.backend.ConfigEnum.GlobalEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)  // ← 생성자를 public 으로
@Schema(description = "사용자의 일정 정보를 담은 DTO")
public class UserScheduleDto {

    @Schema(description = "일정 고유 ID", example = "1")
    private Long id;

    @Schema(description = "반복 일정 여부. Single=false, repeat=true", example = "true")
    private boolean isRepeat;

    @Schema(description = "일정 제목", example = "팀 미팅")
    private String title;

    @Schema(description = "일정 표시 색상-정수로 저장하고 Enum으로 매핑", example = "1")
    private int color;

    @Schema(description = "일정 카테고리 코드 (1=NORMAL, 2=FRIEND, 3=TEAMPLAY, 4=CLUB, 5=SCHOOL)", example = "1")
    private GlobalEnum.ScheduleCategory category;

    @Schema(description = "일정 발생 날짜(YYYY-MM-DD)", example = "2025-07-01")
    private LocalDate date;

    @Schema(description = "요일 (1=월요일 ~ 7=일요일)", example = "3")
    private int day;

    @Schema(description = "시작 시간 (HH:mm:ss)", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간 (HH:mm:ss)", example = "10:30:00")
    private LocalTime endTime;
}
