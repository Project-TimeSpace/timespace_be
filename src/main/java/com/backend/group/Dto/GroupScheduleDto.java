package com.backend.group.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

import com.backend.group.Entity.GroupSchedule;
import com.backend.group.Entity.GroupScheduleUser;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupScheduleDto {

    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;

    @Schema(description = "그룹 일정 ID", example = "101")
    private Long scheduleId;

    @Schema(description = "참여 상태", example = "ACCEPTED")
    private String status;

    @Schema(description = "일정 제목", example = "스터디 모임")
    private String title;

    @Schema(description = "색상 코드", example = "2")
    private int color;

    @Schema(description = "일정 날짜", example = "2025-08-01")
    private LocalDate date;

    @Schema(description = "시작 시간", example = "18:00:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "20:00:00")
    private LocalTime endTime;

    public static GroupScheduleDto from(GroupScheduleUser gsu) {
        GroupSchedule gs = gsu.getGroupSchedule();
        return GroupScheduleDto.builder()
            .groupId(gs.getGroup().getId())
            .scheduleId(gs.getId())
            .status(gsu.getStatus().getDisplayName())
            .title(gs.getTitle())
            .color(gs.getColor() != null ? gs.getColor().getCode() : 0)
            .date(gs.getDate())
            .startTime(gs.getStartTime())
            .endTime(gs.getEndTime())
            .build();
    }

    public static GroupScheduleDto from(GroupSchedule gs) {
        return GroupScheduleDto.builder()
            .groupId(gs.getGroup().getId())
            .scheduleId(gs.getId())
            .status(null)
            .title(gs.getTitle())
            .color(gs.getColor() != null ? gs.getColor().getCode() : 0)
            .date(gs.getDate())
            .startTime(gs.getStartTime())
            .endTime(gs.getEndTime())
            .build();
    }
}

