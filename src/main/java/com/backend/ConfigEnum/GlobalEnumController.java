package com.backend.ConfigEnum;

import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.ConfigEnum.GlobalEnum.University;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/enum")
@RequiredArgsConstructor
@Tag(name = "0. Enum List")
public class GlobalEnumController {

    @Operation(summary = "대학교 목록 조회", description = "FE에서 대학 목록을 선택할 때 사용됩니다.")
    @GetMapping("/univ-list")
    public ResponseEntity<List<UniversityDto>> getUniversityList() {
        List<UniversityDto> universities = Arrays.stream(University.values())
                .map(university -> new UniversityDto(university.getCode(), university.getDisplayName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(universities);
    }

    @Operation(summary = "일정 색상 목록 조회", description = "FE에서 일정 생성/수정 시 선택할 수 있는 기본 색상 목록을 반환합니다.")
    @GetMapping("/color-list")
    public ResponseEntity<List<ColorDto>> getScheduleColors() {
        List<ColorDto> colors = Arrays.stream(ScheduleColor.values())
                .map(c -> new ColorDto(c.ordinal(), c.name(), c.getHex()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(colors);
    }
}
