package com.backend.ConfigEnum.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupCategoryDto {
    @Schema(description = "그룹 카테고리 코드", example = "1")
    private int code;

    @Schema(description = "그룹 카테고리 이름", example = "일반")
    private String name;
}
