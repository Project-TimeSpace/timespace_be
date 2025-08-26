package com.backend.configenum.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorDto {
    @Schema(description = "색상 코드 (ScheduleColor.ordinal())\n" + "0=RED, 1=ORANGE, …, 6=PURPLE", example = "1")
    private int code;

    @Schema(description = "색상 이름 (ScheduleColor.name())\n" + "예: \"RED\", \"ORANGE\" 등", example = "RED")
    private String name;

    @Schema(description = "색상 HEX 코드\n" + "예: \"#FF0000\"", example = "#FF0000")
    private String hex;
}
