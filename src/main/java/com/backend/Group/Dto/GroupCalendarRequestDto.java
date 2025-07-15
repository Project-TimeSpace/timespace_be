package com.backend.Group.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class GroupCalendarRequestDto {

    @Schema(description = "조회 시작일 (YYYY-MM-DD)", example = "2025-07-01")
    private String startDate;

    @Schema(description = "조회 종료일 (YYYY-MM-DD)", example = "2025-07-31")
    private String endDate;

    @Schema(description = "일정 병합 대상 멤버 ID 리스트")
    private List<Long> memberIds;
}
