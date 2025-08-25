package com.backend.shared.Converge;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvergedScheduleDto {
    @Schema(description = "일정 날짜", example = "2025-06-15")
    private LocalDate date;

    @Schema(description = "요일 (1=월요일 ~ 7=일요일)", example = "3")
    private int day;

    @Schema(description = "시작 시간", example = "09:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "10:00")
    private LocalTime endTime;

    @Schema(description = "참여자 수- users의 List size와 동일함", example = "2")
    private int count;

    private List<ParticipantDto> users;

}



