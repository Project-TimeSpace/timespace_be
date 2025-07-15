package com.backend.Friend.Dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "친구 일정 요청 조회용 DTO")
public class ScheduleRequestDto {

    @Schema(description = "요청 ID", example = "42")
    private Long requestId;

    @Schema(description = "보낸 사람 ID", example = "7")
    private Long senderId;

    @Schema(description = "보낸 사람 이름", example = "박철수")
    private String senderName;

    @Schema(description = "일정 제목", example = "함께 점심")
    private String title;

    @Schema(description = "요청 메모", example = "점심 먹고 쇼핑해요")
    private String requestMemo;

    @Schema(description = "약속 날짜", example = "2025-07-15")
    private LocalDate scheduleDate;

    @Schema(description = "시작 시간", example = "12:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "13:00:00")
    private LocalTime endTime;

    @Schema(description = "요청 상태", example = "PENDING")
    private String status;

    @Schema(description = "요청 일시", example = "2025-06-01T14:00:00")
    private LocalDateTime requestedAt;
}
