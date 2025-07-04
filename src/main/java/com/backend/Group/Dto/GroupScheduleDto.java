package com.backend.Group.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupScheduleDto {
    private Long scheduleId;
    private String title;
    private int color;
    private LocalDate date;
    private int day;
    private LocalTime startTime;
    private LocalTime endTime;
}
