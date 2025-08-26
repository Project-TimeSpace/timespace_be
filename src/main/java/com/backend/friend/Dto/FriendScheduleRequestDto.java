package com.backend.friend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "친구에게 보낼 약속(일정) 요청 DTO")
public class FriendScheduleRequestDto {

    @Schema(description = "약속 제목", example = "점심 식사")
    private String title;

    @Schema(description = "약속 내용", example = "이날 이거 먹으러 갈래?")
    private String requestMemo;

    @Schema(description = "일정 표시 색상-정수로 저장하고 Enum으로 매핑", example = "1")
    private Integer color;

    @Schema(description = "약속 날짜 (YYYY-MM-DD)", example = "2025-06-15")
    private LocalDate date;

    @Schema(description = "시작 시간 (HH:mm)", example = "12:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간 (HH:mm)", example = "13:00")
    private LocalTime endTime;
}
