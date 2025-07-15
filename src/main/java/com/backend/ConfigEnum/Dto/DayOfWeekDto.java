package com.backend.ConfigEnum.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DayOfWeekDto {
    @Schema(description = "요일 코드 (1=월요일 ~ 7=일요일)", example = "1")
    private int code;

    @Schema(description = "요일 이름 (MONDAY~SUNDAY)", example = "MONDAY")
    private String name;
}
